package net.thisisz.hermes.bungee.messaging.filter;

import net.md_5.bungee.config.Configuration;
import net.thisisz.hermes.bungee.HermesChat;
import java.util.regex.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class HermesFilter implements Filter {

    private HermesChat plugin;
    private Map<Pattern, String> regexFilters  = new HashMap<>();

    public HermesFilter() {
        loadFilters();
    }

    private void loadFilters() {
        Configuration filtersConfig = getPlugin().getConfiguration().getSection("filters");
        Collection<String> filterIndexes = filtersConfig.getKeys();
        for (String filterIndex:filterIndexes) {
            Configuration filter = filtersConfig.getSection(filterIndex);
            regexFilters.put(Pattern.compile(filter.getString("replace")), filter.getString("with"));
        }
    }

    private HermesChat getPlugin() {
        return HermesChat.getPlugin();
    }

    @Override
    public String filterMessage(String message) {
        for(Pattern filter:regexFilters.keySet()) {
            message = filter.matcher(message).replaceAll(regexFilters.get(filter));
        }
        return message;
    }

}
