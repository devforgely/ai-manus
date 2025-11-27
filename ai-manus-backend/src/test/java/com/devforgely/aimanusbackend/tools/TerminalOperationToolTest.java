package com.devforgely.aimanusbackend.tools;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TerminalOperationToolTest {
    @Test
    void executeTerminalCommand() {
        TerminalOperationTool terminalOperationTool = new TerminalOperationTool();
        String command = "dir";
        String result = terminalOperationTool.executeTerminalCommand(command);
        Assertions.assertNotNull(result);
    }
}
