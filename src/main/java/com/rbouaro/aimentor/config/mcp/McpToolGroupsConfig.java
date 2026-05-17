package com.rbouaro.aimentor.config.mcp;

import com.embabel.agent.core.CoreToolGroups;
import com.embabel.agent.core.ToolGroup;
import com.embabel.agent.core.ToolGroupPermission;
import com.embabel.agent.spi.config.spring.ConditionalOnMcpConnection;
import com.embabel.agent.tools.mcp.McpToolGroup;
import com.embabel.agent.tools.mcp.ToolCallContextMcpMetaConverter;
import io.modelcontextprotocol.client.McpSyncClient;
import kotlin.jvm.functions.Function1;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Set;

@Configuration
public class McpToolGroupsConfig {

    @Bean
    @ConditionalOnMcpConnection("duckduckgo-mcp")
    public ToolGroup webToolGroup(List<McpSyncClient> mcpSyncClients) {
        Function1<ToolCallback, Boolean> filter = tool -> {
            String name = tool.getToolDefinition().name().toLowerCase();
            return name.contains("search") || name.contains("duckduckgo");
        };
        return new McpToolGroup(
                CoreToolGroups.INSTANCE.getWEB_DESCRIPTION(),
                "mcp-web",
                "duckduckgo-mcp",
                Set.of(ToolGroupPermission.INTERNET_ACCESS),
                mcpSyncClients,
                filter,
                ToolCallContextMcpMetaConverter.Companion.noOp()
        );
    }
}