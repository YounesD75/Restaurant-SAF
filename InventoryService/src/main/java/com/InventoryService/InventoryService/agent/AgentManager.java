package com.InventoryService.InventoryService.agent;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class AgentManager {

    private final Map<String, AbstractAgent> agents = new HashMap<>();

    public void register(AbstractAgent agent) {
        agents.put(agent.getName(), agent);
        System.out.println("🤖 Agent registered: " + agent.getName());
    }

    public void send(String agentName, Message message) {
        AbstractAgent agent = agents.get(agentName);

        if (agent != null) {
            agent.send(message);
        } else {
            System.out.println("❌ Agent not found: " + agentName);
        }
    }
}



