package com.acfreeman.socialmediascanner.AzureDB;


public class PHONES {

    /**
     * Item phoneNums
     */
    @com.google.gson.annotations.SerializedName("number")
    private String mNumber;

    /**
     * Item Person_Id
     */
    @com.google.gson.annotations.SerializedName("person_Id")
    private int mPerson_Id;
    
    /**
     * Item type
     */
    @com.google.gson.annotations.SerializedName("type")
    private String mType;

    /**
     * Indicates if the item is completed
     */
    @com.google.gson.annotations.SerializedName("complete")
    private boolean mComplete;


    /**
     * PHONES constructor
     */
    public PHONES() {

    }

    @Override
    public String toString() {
        return getNumber();
    }

    /**
     * Initializes a new PHONES
     *
     * @param number
     *            The item number
     * @param person_Id
     *            The item person_Id
     * @param type
     *            The item type
     */
    public PHONES(String number, String type, int person_Id) {
        this.setNumber(number);
        this.setPerson_Id(person_Id);
        this.setType(type);
    }

    /**
     * Returns the item number
     */
    public String getNumber() {
        return mNumber;
    }


    /**
     * Sets the item number
     *
     * @param number
     *            number to set
     */
    public final void setNumber(String number) {
        mNumber = number;
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
     * Returns the item type
     */
    public String getType() {
        return mType;
    }

    /**
     * Sets the item type
     *
     * @param type
     *            number to set
     */
    public final void setType(String type) {
        mType = type;
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
        return o instanceof PHONES && ((PHONES) o).mPerson_Id == mPerson_Id;
    }
}