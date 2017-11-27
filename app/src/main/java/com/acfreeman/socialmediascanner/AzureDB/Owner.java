package com.acfreeman.socialmediascanner.AzureDB;


public class Owner {

    /**
     * Item name
     */
    @com.google.gson.annotations.SerializedName("name")
    private String mName;

    /**
     * Item Id
     */
    @com.google.gson.annotations.SerializedName("Owner_Id")
    private int mOwner_Id;

    /**
     * Indicates if the item is completed
     */
    @com.google.gson.annotations.SerializedName("complete")
    private boolean mComplete;


    /**
     * Owner constructor
     */
    public Owner() {

    }

    @Override
    public String toString() {
        return getName();
    }

    /**
     * Initializes a new Owner
     *
     * @param name
     *            The item name
     * @param owner_Id
     *            The item id
     */
    public Owner(String name, int owner_Id) {
        this.setName(name);
        this.setOwner_Id(owner_Id);
    }

    /**
     * Returns the item name
     */
    public String getName() {
        return mName;
    }

    /**
     * Sets the item name
     *
     * @param name
     *            name to set
     */
    public final void setName(String name) {
        mName = name;
    }

    /**
     * Returns the item id
     */
    public int getOwner_Id() {
        return mOwner_Id;
    }

    /**
     * Sets the item id
     *
     * @param owner_Id
     *            id to set
     */
    public final void setOwner_Id(int owner_Id) {
        mOwner_Id = owner_Id;
    }


    /**
     * Indicates if the item is marked as completed
     */
    public boolean isComplete() {
        return mComplete;
    }

    /**
     * Marks the item as completed or incompleted
     */
    public void setComplete(boolean complete) {
        mComplete = complete;
    }


    @Override
    public boolean equals(Object o) {
        return o instanceof Owner && ((Owner) o).mOwner_Id == mOwner_Id;
    }
}