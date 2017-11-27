package com.acfreeman.socialmediascanner.AzureDB;

/**
 * Created by jianziyu on 2017/11/13.
 */
public class CONTACTS {

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
     * Item Id
     */
    @com.google.gson.annotations.SerializedName("owner_Id")
    private int mOwner_Id;

    /**
     * Indicates if the item is completed
     */
    @com.google.gson.annotations.SerializedName("complete")
    private boolean mComplete;

    /**
     * CONTACTS constructor
     */
    public CONTACTS() {

    }

    @Override
    public String toString() {
        return getName();
    }

    /**
     * Initializes a new CONTACTS
     *
     * @param name
     *            The item name
     * @param person_Id
     *            The item person_Id
     * @param owner_Id
     */
    public CONTACTS(String name, int person_Id, int owner_Id) {
        this.setName(name);
        this.setPerson_Id(person_Id);
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
     * Returns the item person_Id
     */
    public int getPerson_Id() {
        return mPerson_Id;
    }

    /**
     * Sets the item person_Id
     *
     * @param person_Id
     *            person_Id to set
     */
    public final void setPerson_Id(int person_Id) {
        mPerson_Id = person_Id;
    }

    /**
     * Indicates if the item is marked as completed
     */

    /**
     * Returns the item owner_Id
     */
    public int getOwner_Id() {
        return mOwner_Id;
    }

    /**
     * Sets the item owner_Id
     *
     * @param owner_Id
     *            owner_Id to set
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
        return o instanceof CONTACTS && ((CONTACTS) o).mPerson_Id == mPerson_Id;
    }
}