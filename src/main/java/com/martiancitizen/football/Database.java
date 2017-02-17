package com.martiancitizen.football;

import com.martiancitizen.football.model.*;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.NPOIFSFileSystem;
import org.apache.poi.ss.usermodel.*;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.util.*;
import java.util.function.*;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;

/**
 * Created by johnchamberlain on 1/21/16.
 */
public class Database {

    private static final String dbPath = System.getProperty("db.fullpath") != null ? System.getProperty("db.fullpath")
            : System.getProperty("user.dir") + "/src/test/resources";

    private final Helper helper = new Helper();

    private SortedMap<String, Conference> conferences = new TreeMap<>();
    private SortedMap<String, Division> divisions = new TreeMap<>();
    private SortedMap<String, Team> teams = new TreeMap<>();
    private SortedMap<String, Player> players = new TreeMap<>();

    private List<String> parseErrors = new ArrayList<>();

    /**
     * Constructor that loads the spreadsheet into the internal database
     */
    public Database() throws Exception {
        loadDatabase();
        if (!parseErrors.isEmpty()) {
            parseErrors.forEach(WebApplication.LOGGER::error);
            throw new Exception("Could not load spreadsheet");
        }
    }


    private class ObjectRow {
        Integer index;
        RowType type;
        String[] cells;

        ObjectRow(Integer index, RowType type, String[] cells) {
            this.index = index;
            this.type = type;
            this.cells = cells;
        }
    }

    public Optional<Team> getTeamForId(String id) {
        return teams.containsKey(id) ? Optional.of(teams.get(id)) : Optional.empty();
    }


    /**
     * This method implements the high-level functional flow for loading the spreadsheet
     */
    private void loadDatabase() {

        // Get the rows that contain valid data, then convert each row into an ObjectRow for processing.
        List<ObjectRow> objRows = helper.loadSpreadsheet.get().stream()
                .filter(helper.isNotABlankRow)
                .filter(helper.startsWithLabel)
                .filter(helper.isNotACommentRow)
                .map(helper.getObjectRowFromRow)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(toList());

        // Load up conferences
        conferences.putAll(objRows.stream()
                .filter(helper.isConferenceRow)
                .map(helper.getConferenceFromRow)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(toMap(Pair::getKey, Pair::getValue)));

        // Load up divisions
        divisions.putAll(objRows.stream()
                .filter(helper.isDivisionRow)
                .map(helper.getDivisionFromRow)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(toMap(Pair::getKey, Pair::getValue)));

        // Load up teams
        teams.putAll(objRows.stream()
                .filter(helper.isTeamRow)
                .map(helper.getTeamFromRow)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(toMap(Pair::getKey, Pair::getValue)));

        // Load up players
        players.putAll(objRows.stream()
                .filter(helper.isPlayerRow)
                .map(helper.getPlayerFromRow)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(toMap(Pair::getKey, Pair::getValue)));
    }


    /**
     * This class contains helper methods that are implementations of functional interfaces
     */
    private class Helper {

        Supplier<List<Row>> loadSpreadsheet = () -> {
            // Load the raw data from the spreadsheet. Unfortunately, there isn't a sheet method that returns all the rows as an array or list,
            // but there is an Iterator so we can do it ourselves.
            List<Row> rows = new ArrayList<>();
            try (NPOIFSFileSystem fs = new NPOIFSFileSystem(new File(dbPath))) {
                HSSFWorkbook wb = new HSSFWorkbook(fs.getRoot(), true);
                Sheet sheet = wb.getSheetAt(0);
                Boolean startOfDatabaseFound = false;
                for (Row row : sheet) {
                    // we ignore the header rows
                    if (startOfDatabaseFound) {
                        rows.add(row);
                    } else if (row.getCell(row.getFirstCellNum()).getCellType() == Cell.CELL_TYPE_STRING
                            && row.getCell(row.getFirstCellNum()).getStringCellValue().startsWith("--- START")) {
                        startOfDatabaseFound = true;
                    }
                }
            } catch (Exception ex) {
                parseErrors.add("Mock database file error: " + ex.getMessage());
            }
            return rows;
        };


        // Predicates to support filtering spreadsheet rows and ObjectRows
        Predicate<Row> isNotABlankRow = row -> !(row.getCell(row.getFirstCellNum()).getCellType() == Cell.CELL_TYPE_BLANK);
        Predicate<Row> startsWithLabel = row -> {
            if (row.getCell(row.getFirstCellNum()).getCellType() == Cell.CELL_TYPE_STRING) return true;
            parseErrors.add("First cell must be a string in row " + (row.getRowNum() + 1));
            return false;
        };
        Predicate<Row> isNotACommentRow = row -> !row.getCell(row.getFirstCellNum()).getStringCellValue().startsWith("#");
        Predicate<ObjectRow> isConferenceRow = obj -> obj.type == RowType.CONFERENCE;
        Predicate<ObjectRow> isDivisionRow = obj -> obj.type == RowType.DIVISION;
        Predicate<ObjectRow> isTeamRow = obj -> obj.type == RowType.TEAM;
        Predicate<ObjectRow> isPlayerRow = obj -> obj.type == RowType.PLAYER;


        // Mapping function that converts spreadsheet rows into ObjectRows
        Function<Row, Optional<ObjectRow>> getObjectRowFromRow = row -> {
            int numCells = row.getPhysicalNumberOfCells();
            String[] cells = new String[numCells];
            try {
                int index = 0;
                for (Cell cell : row) {
                    cells[index++] = cell.getStringCellValue();
                }
            } catch (Exception ex) {
                parseErrors.add(ex.getMessage() + " at row " + (row.getRowNum() + 1));
            }
            try {
                RowType rowType = RowType.valueOf(cells[0]);
                if (numCells < rowType.getNumRequiredDataCells()) {
                    parseErrors.add("Insufficient number of cells at row " + (row.getRowNum() + 1));
                    return Optional.empty();
                }
                return Optional.of(new ObjectRow(row.getRowNum() + 1, rowType, cells));  // convert row index to one-based to match the Excel UI
            } catch (Exception e) {
                parseErrors.add("Unknown row type " + cells[0] + " at row " + (row.getRowNum() + 1));
                return Optional.empty();
            }
        };


        // Helper method to return a list of child objects of the desired type that belong to a parent
        <T> List<T> getChildObjects(String parentId, List<Pair<String, T>> objList) {
            return objList.stream()
                    .filter(p -> p.getKey().equals(parentId))
                    .map(Pair::getValue)
                    .collect(toList());
        }


        // Mapping functions that convert cell arrays into database objects

        Predicate<Pair<String, Consumer<String>>> ifValuePresent = pair -> pair.getLeft() != null && !pair.getLeft().isEmpty() && !pair.getLeft().equalsIgnoreCase("na");

        UnaryOperator<String> isConference = (arg) -> {
            if (!conferences.containsKey(arg)) {
                throw new IllegalArgumentException("Invalid conference: " + arg);
            }
            return arg;
        };

        UnaryOperator<String> isDivision = (arg) -> {
            if (!divisions.containsKey(arg)) {
                throw new IllegalArgumentException("Invalid division: " + arg);
            }
            return arg;
        };

        UnaryOperator<String> isTeam = (arg) -> {
            if (!teams.containsKey(arg)) {
                throw new IllegalArgumentException("Invalid team: " + arg);
            }
            return arg;
        };

        Function<ObjectRow, Optional<Pair<String, Conference>>> getConferenceFromRow = row -> {

            final String prefix = "Error in database row " + row.index + ": ";
            final String[] cells = row.cells;
            Conference conf = new Conference();

            List<Pair<String, Consumer<String>>> setters = Stream.of(
                    new ImmutablePair<String, Consumer<String>>(cells[1], conf::setId),
                    new ImmutablePair<String, Consumer<String>>(cells[2], conf::setName)).collect(toList());

            try {
                setters.stream().filter(ifValuePresent).forEach(pair -> pair.getRight().accept(pair.getLeft()));
                return Optional.of(new ImmutablePair<>(conf.validate().getId(), conf));
            } catch (Exception e) {
                parseErrors.add(prefix + e.toString());
            }

            return Optional.empty();
        };


        Function<ObjectRow, Optional<Pair<String, Division>>> getDivisionFromRow = row -> {
            final String prefix = "Error in database row " + row.index + ": ";
            final String[] cells = row.cells;
            Division division = new Division();

            List<Pair<String, Consumer<String>>> setters = Stream.of(
                    new ImmutablePair<String, Consumer<String>>(cells[1], division::setId),
                    new ImmutablePair<String, Consumer<String>>(cells[2], division::setName)).collect(toList());

            try {
                setters.stream().filter(ifValuePresent).forEach(pair -> pair.getRight().accept(pair.getLeft()));
                return Optional.of(new ImmutablePair<>(division.validate().getId(), division));
            } catch (Exception e) {
                parseErrors.add(prefix + e.toString());
            }

            return Optional.empty();
        };


        Function<ObjectRow, Optional<Pair<String, Team>>> getTeamFromRow = row -> {
            final String prefix = "Error in database row " + row.index + ": ";
            final String[] cells = row.cells;
            Team team = new Team();

            try {
                List<Pair<String, Consumer<String>>> setters = Stream.of(
                        new ImmutablePair<String, Consumer<String>>(cells[1], team::setId),
                        new ImmutablePair<String, Consumer<String>>(cells[2], team::setName),
                        new ImmutablePair<String, Consumer<String>>(cells[3], arg -> isConference.andThen(team.setConference).apply(arg)),
                        new ImmutablePair<String, Consumer<String>>(cells[4], arg -> isDivision.andThen(team.setDivision).apply(arg)))
                        .collect(toList());

                setters.stream().filter(ifValuePresent).forEach(pair -> pair.getRight().accept(pair.getLeft()));
                return Optional.of(new ImmutablePair<>(team.validate().getId(), team));
            } catch (Exception e) {
                parseErrors.add(prefix + e.toString());
            }

            return Optional.empty();
        };


        Function<ObjectRow, Optional<Pair<String, Player>>> getPlayerFromRow = row -> {
            final String prefix = "Error in database row " + row.index + ": ";
            final String[] cells = row.cells;
            Player player = new Player();

            List<Pair<String, Consumer<String>>> setters = Stream.of(
                    new ImmutablePair<String, Consumer<String>>(cells[1], arg -> isTeam.andThen(player.setTeam).apply(arg)),
                    new ImmutablePair<String, Consumer<String>>(cells[2], player::setNumber),
                    new ImmutablePair<String, Consumer<String>>(cells[3], player::setName),
                    new ImmutablePair<String, Consumer<String>>(cells[4], player::setPosition),
                    new ImmutablePair<String, Consumer<String>>(cells[5], player::setHeight),
                    new ImmutablePair<String, Consumer<String>>(cells[6], player::setWeight),
                    new ImmutablePair<String, Consumer<String>>(cells[7], player::setAge),
                    new ImmutablePair<String, Consumer<String>>(cells[8], player::setCollege)).collect(toList());
            try {
                setters.stream().filter(ifValuePresent).forEach(pair -> pair.getRight().accept(pair.getLeft()));
                return Optional.of(new ImmutablePair<>(player.validate().getId(), player));
            } catch (Exception e) {
                parseErrors.add(prefix + e.toString());
            }

            return Optional.empty();
        };

    }

}
