package net.thisisz.hermes.bungee.messaging.filter;

import net.md_5.bungee.config.Configuration;
import net.thisisz.hermes.bungee.HermesChat;
import java.util.regex.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class HermesFilter implements Filter {

    private HermesChat plugin;
    private Map<String, String> regexFilters;

    public HermesFilter(HermesChat plugin) {
        this.plugin = plugin;
        this.regexFilters = new HashMap<String, String>();
        loadFilters();
    }

    private void loadFilters() {
        Configuration filtersConfig = getPlugin().getConfiguration().getSection("filters");
        Collection<String> filterIndexes = filtersConfig.getKeys();
        for (String filterIndex:filterIndexes) {
            Configuration filter = filtersConfig.getSection(filterIndex);
            regexFilters.put(filter.getString("replace"), filter.getString("with"));
        }
    }

    public HermesChat getPlugin() {
        return plugin;
    }

    @Override
    public String filterMessage(String message) {
        for(String filter:regexFilters.keySet()) {
            message = message.replaceAll(filter, regexFilters.get(filter));
        }
        return message;
    }

}
