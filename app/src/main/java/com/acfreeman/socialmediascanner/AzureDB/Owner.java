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
    @com.google.gson.annotations.SerializedName("person_Id")
    private int mPerson_Id;

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
     * @param person_Id
     *            The item id
     */
    public Owner(String name, int person_Id) {
        this.setName(name);
        this.setPerson_Id(person_Id);
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
    public int getPerson_Id() {
        return mPerson_Id;
    }

    /**
     * Sets the item id
     *
     * @param person_Id
     *            id to set
     */
    public final void setPerson_Id(int person_Id) {
        mPerson_Id = person_Id;
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
        return o instanceof Owner && ((Owner) o).mPerson_Id == mPerson_Id;
    }
}