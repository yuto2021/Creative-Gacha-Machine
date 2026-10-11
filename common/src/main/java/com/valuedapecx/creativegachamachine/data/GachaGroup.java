package com.valuedapecx.creativegachamachine.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

import java.util.ArrayList;
import java.util.List;

public class GachaGroup {

    private String name;

    private int rollCount = 1;

    private final List<GachaPage> pages = new ArrayList<>();

    public GachaGroup() {
        name = "New Group";

        pages.add(new GachaPage());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRollCount() {
        return rollCount;
    }

    public void setRollCount(int rollCount) {
        this.rollCount = rollCount;
    }

    public List<GachaPage> getPages() {
        return pages;
    }

    public CompoundTag save(
            HolderLookup.Provider registries
    ) {

        CompoundTag tag = new CompoundTag();

        tag.putString("Name", name);
        tag.putInt("RollCount", rollCount);

        ListTag pagesTag = new ListTag();

        for (GachaPage page : pages) {
            pagesTag.add(page.save(registries));
        }

        tag.put("Pages", pagesTag);

        return tag;
    }

    public static GachaGroup load(
            CompoundTag tag,
            HolderLookup.Provider registries
    ) {
        GachaGroup group = new GachaGroup();

        group.name = tag.getString("Name");
        group.rollCount = tag.getInt("RollCount");

        group.pages.clear();

        ListTag pagesTag = tag.getList(
                "Pages",
                Tag.TAG_COMPOUND
        );

        for (int i = 0; i < pagesTag.size(); i++) {
            group.pages.add(
                    GachaPage.load(
                            pagesTag.getCompound(i),
                            registries
                    )
            );
        }

        if (group.pages.isEmpty()) {
            group.pages.add(new GachaPage());
        }

        return group;
    }
}
