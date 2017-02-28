package com.martiancitizen.football;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(format = { "pretty", "html:log/cucumber"}, tags = { "~@wip"})
public class RunCukesTest {
}
