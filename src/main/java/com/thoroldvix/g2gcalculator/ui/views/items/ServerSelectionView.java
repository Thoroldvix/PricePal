package com.thoroldvix.g2gcalculator.ui.views.items;

import com.thoroldvix.g2gcalculator.server.Faction;
import com.thoroldvix.g2gcalculator.server.ServerResponse;
import com.thoroldvix.g2gcalculator.server.ServerService;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.Getter;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

@Getter
@SpringComponent
@UIScope
public class ServerSelectionView extends HorizontalLayout {
    private final ItemGridView itemGrid;
    private final ServerService serverServiceImpl;
    private ComboBox<ServerResponse> serverSelect = new ComboBox<>();
    private Select<Faction> factionSelect = new Select<>();

    public ServerSelectionView(ItemGridView itemGrid, ServerService serverServiceImpl) {
        this.itemGrid = itemGrid;
        this.serverServiceImpl = serverServiceImpl;
        setClassName("server-selection-view");
        setAlignItems(Alignment.BASELINE);
        setJustifyContentMode(JustifyContentMode.CENTER);
        setWidth("auto");
        configureServerSelect();
        configureFactionSelect();

        add(serverSelect, factionSelect);
    }


    private void configureFactionSelect() {
        factionSelect = new Select<>();
        addClassName("faction-select");
        factionSelect.setPlaceholder("Select Faction");
        factionSelect.setRequiredIndicatorVisible(true);
        factionSelect.setErrorMessage("Please select a faction");
        factionSelect.setItems(Faction.values());
        factionSelect.addValueChangeListener(event -> {
            if (serverSelect.getValue() != null) {
                itemGrid.updateGrid(getServerName(serverSelect.getValue().name(), event.getValue()));
            }
        });
    }

    private void configureServerSelect() {
        serverSelect = new ComboBox<>();
        Set<ServerResponse> servers = new TreeSet<>(Comparator.comparing(ServerResponse::name));
        servers.addAll(serverServiceImpl.getAllServers());
        serverSelect.addClassName("server-select");
        serverSelect.setAllowCustomValue(false);
        serverSelect.setPlaceholder("Select Server");
        serverSelect.setRequired(true);
        serverSelect.setRequiredIndicatorVisible(true);
        serverSelect.setErrorMessage("Please select a server");
        serverSelect.setItems(servers);
        serverSelect.setItemLabelGenerator(ServerResponse::name);
        serverSelect.addValueChangeListener(event -> {
            if (factionSelect.getValue() != null) {
                itemGrid.updateGrid(getServerName(event.getValue().name(), factionSelect.getValue()));
            }
        });
    }

    private String getServerName(String serverName, Faction faction) {
        return serverName.replaceAll(" ", "-").toLowerCase() + "-" + faction.toString().toLowerCase();
    }
}