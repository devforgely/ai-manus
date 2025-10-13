package com.devforgely.aimanusbackend.agents;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

/**
 * ReAct (Reasoning & Acting)
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
public abstract class ReActAgent extends BaseAgent {
    /**
     * Process current state & decide next action
     * @return true if require action else false
     */
    public abstract boolean think();

    /**
     * Act
     * @return result of action
     */
    public abstract String act();

    /**
     * Process single step: think & act
     * @return result of step execution
     */
    @Override
    public String step() {
        try {
            boolean shouldAct = think();
            if (!shouldAct) {
                return "Complete Think, No Action";
            }
            return act();
        } catch (Exception e) {
            e.printStackTrace();
            return "Step execution failed: " + e.getMessage();
        }
    }
}
