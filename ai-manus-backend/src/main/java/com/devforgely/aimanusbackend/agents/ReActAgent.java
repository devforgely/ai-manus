package com.devforgely.aimanusbackend.agents;

import com.devforgely.aimanusbackend.agents.model.AgentResult;
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
     * @return whether to continue act and the message from agent
     */
    public abstract AgentResult think();

    /**
     * Act
     * @return result of action
     */
    public abstract AgentResult act();

    /**
     * Process single step: think & act
     * @return result of step execution
     */
    @Override
    public AgentResult step() {
        try {
            AgentResult thinkResult = think();
            if (!thinkResult.act()) {
                return thinkResult;
            }
            return act();
        } catch (Exception e) {
            e.printStackTrace();
            return new AgentResult(false, "Step execution failed: " + e.getMessage());
        }
    }
}
