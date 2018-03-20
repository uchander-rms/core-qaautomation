package QAAutomationTestInterface;

import QAAutomationObjects.TestCase;


public interface  ITestEngine {
      boolean GetInput(TestCase TestCase);

      boolean Run(TestCase TestCase);

      boolean Validate(TestCase TestCase);
}