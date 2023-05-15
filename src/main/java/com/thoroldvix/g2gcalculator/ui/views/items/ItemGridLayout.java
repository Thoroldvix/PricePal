package com.thoroldvix.g2gcalculator.ui.views.items;

import com.thoroldvix.g2gcalculator.item.ItemService;
import com.thoroldvix.g2gcalculator.item.dto.ItemInfo;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import org.vaadin.klaudeta.PaginatedGrid;

import java.util.Comparator;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@SpringComponent
@UIScope
public class ItemGridLayout extends VerticalLayout {
    private final ItemService itemServiceImpl;

    private final ItemFilteringLayout itemFilteringLayout;

    private final PaginatedGrid<ItemInfo, ?> itemGrid = new PaginatedGrid<>();

    private String serverName;

    public ItemGridLayout(ItemService itemServiceImpl) {
        this.itemServiceImpl = itemServiceImpl;
        itemFilteringLayout = new ItemFilteringLayout(this);

        addClassName("item-grid-view");
        setSizeFull();
        setAlignItems(Alignment.START);
        configureGrid();
        configureColumns();
        add(itemFilteringLayout, itemGrid);
    }

    private void configureGrid() {
        itemGrid.setVisible(false);
        itemGrid.setSelectionMode(Grid.SelectionMode.NONE);
        itemGrid.setPaginationVisibility(false);
        itemGrid.addClassName("item-grid");
        itemGrid.setWidthFull();
        itemGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        itemGrid.setHeight("auto");
        itemGrid.setPageSize(20);
        itemGrid.setPaginatorSize(5);
        itemGrid.setPage(1);
    }

    public void populateGrid(String serverName) {
        this.serverName = serverName;
        itemGrid.setItems(itemServiceImpl.getAllItemsInfo(serverName));
        itemGrid.setVisible(true);
        itemFilteringLayout.setVisible(true);
        itemGrid.setPaginationVisibility(true);
    }

    public void onFilterChange(Predicate<ItemInfo> filter) {
        Set<ItemInfo> items = itemServiceImpl.getAllItemsInfo(serverName).stream()
                .filter(filter).collect(Collectors.toSet());
        itemGrid.setItems(items);
    }

    private void configureColumns() {
        itemGrid.addColumn(new ComponentRenderer<>(ItemNameRenderer::new))
                .setHeader("Name")
                .setComparator(Comparator.comparing(ItemInfo::name))
                .setWidth("270px");
        itemGrid.addColumn(new ComponentRenderer<>(itemInfo -> new ItemPriceRenderer(itemInfo.auctionHouseInfo().minBuyout())))
                .setHeader("Min Buyout")
                .setComparator(Comparator.comparingLong(itemInfo -> itemInfo.auctionHouseInfo().minBuyout()))
                .setWidth("150px");
        itemGrid.addColumn(new ComponentRenderer<>(itemInfo -> new ItemPriceRenderer(itemInfo.auctionHouseInfo().marketValue())))
                .setHeader("Market Value")
                .setSortable(true)
                .setComparator(Comparator.comparingLong(itemInfo -> itemInfo.auctionHouseInfo().marketValue()))
                .setWidth("150px");
        itemGrid.addColumn(itemInfo -> itemInfo.auctionHouseInfo().quantity()).setHeader("Market Quantity")
                .setAutoWidth(true);
        itemGrid.addColumn(itemInfo -> itemInfo.auctionHouseInfo().numAuctions()).setHeader("Number of Auctions")
                .setAutoWidth(true);

        itemGrid.getColumns().forEach(col -> {
            col.setSortable(true);
        });
    }
}