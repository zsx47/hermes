package net.thisisz.hermes.bungee.messaging.filter;

import net.thisisz.hermes.bungee.HermesChat;
import net.thisisz.hermes.bungee.messaging.MessagingController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FilterManager {

    private MessagingController controller;
    private List<Filter> filterCollection = new ArrayList<Filter>();

    public FilterManager(MessagingController parent) {
        this.controller = parent;
        loadDefaultFilter();
    }

    public FilterManager(MessagingController parent, Filter... filters) {
        this.controller = parent;
        filterCollection.addAll(Arrays.asList(filters));
        loadDefaultFilter();
    }

    public HermesChat getPlugin() {
        return controller.getPlugin();
    }

    private void loadDefaultFilter() {
        this.filterCollection.add(new HermesFilter(getPlugin()));
    }

    public String filterMessage(String message) {
        for (Filter filter:filterCollection) {
            message = filter.filterMessage(message);
        }
        return message;
    }

}
