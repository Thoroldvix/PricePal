package com.thoroldvix.g2gcalculator.ui.views;

import com.thoroldvix.g2gcalculator.server.Faction;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.data.renderer.ComponentRenderer;

public class FactionSelect extends Select<Faction> {



    public FactionSelect() {
        addClassName("faction-select");
        setPlaceholder("Select Faction");
        setEmptySelectionAllowed(true);
        setEmptySelectionCaption("Any");
        setRenderer(new ComponentRenderer<>(FactionRenderer::new));
        setItems(Faction.values());
    }




}
