package com.thoroldvix.pricepal.item;

import com.thoroldvix.pricepal.item.dto.ItemInfo;
import com.thoroldvix.pricepal.item.entity.Item;
import com.thoroldvix.pricepal.item.entity.ItemQuality;
import com.thoroldvix.pricepal.item.entity.ItemRepository;
import com.thoroldvix.pricepal.item.entity.ItemType;
import com.thoroldvix.pricepal.item.service.ItemServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    ItemServiceImpl itemServiceImpl;

    @Test
    void getItemByName_whenValidItemName_returnsItemInfo() {
        Item item = Item.builder()
                .name("test")
                .id(1)
                .quality(ItemQuality.EPIC)
                .type(ItemType.WEAPON)
                .build();

        ItemInfo expectedItemInfo = ItemInfo.builder()
                .name("test")
                .id(1)
                .quality(ItemQuality.EPIC)
                .type(ItemType.WEAPON)
                .build();

        String itemName = "test";

        when(itemRepository.findByName(itemName)).thenReturn(Optional.of(item));

        ItemInfo actualItemInfo = itemServiceImpl.getItem(itemName);

        assertThat(actualItemInfo).isEqualTo(expectedItemInfo);
    }

    @Test
    void getItemByName_whenEmptyItemName_throwsIllegalArgumentException() {
        String itemName = "";

        assertThatThrownBy(() -> itemServiceImpl.getItem(itemName))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void getItemByName_whenBlankItemName_throwsIllegalArgumentException() {
        String itemName = " ";

        assertThatThrownBy(() -> itemServiceImpl.getItem(itemName))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void getItemByName_whenNullItemName_throwsIllegalArgumentException() {


        assertThatThrownBy(() -> itemServiceImpl.getItem(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void getItemById_whenValidServerNameAndItemId_returnsItemInfo() {
        Item item = Item.builder()
                .name("test")
                .id(1)
                .quality(ItemQuality.EPIC)
                .type(ItemType.WEAPON)
                .build();
        ItemInfo expectedItemInfo = ItemInfo.builder()
                .name("test")
                .id(1)
                .quality(ItemQuality.EPIC)
                .type(ItemType.WEAPON)
                .build();


        int itemId = expectedItemInfo.id();

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        ItemInfo actualItemInfo = itemServiceImpl.getItem(itemId);

        assertThat(actualItemInfo).isEqualTo(expectedItemInfo);
    }

    @Test
    void getItemById_whenInvalidItemId_throwsIllegalArgumentException() {
        int itemId = -1;

        assertThatThrownBy(() -> itemServiceImpl.getItem(itemId))
                .isInstanceOf(IllegalArgumentException.class);
    }



    @Test
    void saveItem_whenValidItem_savesItem() {
        Item item = Item.builder()
                .id(1)
                .name("test")
                .build();
        itemServiceImpl.saveItem(item);

        verify(itemRepository).save(item);
    }

    @Test
    void getItemCount_returnsCorrectItemCount() {
        long expectedItemCount = 1;
        when(itemRepository.count()).thenReturn(expectedItemCount);

        long actualItemCount = itemServiceImpl.getItemCount();
        assertThat(actualItemCount).isEqualTo(expectedItemCount);
    }

}