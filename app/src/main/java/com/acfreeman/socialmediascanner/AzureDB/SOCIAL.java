package com.acfreeman.socialmediascanner.AzureDB;


public class SOCIAL {

    /**
     * Item phoneNums
     */
    @com.google.gson.annotations.SerializedName("username")
    private String mUsername;

    /**
     * Item Id
     */
    @com.google.gson.annotations.SerializedName("person_Id")
    private int mPerson_Id;
    
    /**
     * Item type
     */
    @com.google.gson.annotations.SerializedName("social_type")
    private String mSocial_type;

    /**
     * Indicates if the item is completed
     */
    @com.google.gson.annotations.SerializedName("complete")
    private boolean mComplete;


    /**
     * SOCIAL constructor
     */
    public SOCIAL() {

    }

    @Override
    public String toString() {
        return getUsername();
    }

    /**
     * Initializes a new SOCIAL
     *
     * @param username
     *            The item phones
     * @param person_Id
     *            The item id
     * @param social_type
     *            The item type
     */
    public SOCIAL(String username, String social_type, int person_Id) {
        this.setUsername(username);
        this.setPerson_Id(person_Id);
        this.setSocial_type(social_type);
    }

    /**
     * Returns the item phones
     */
    public String getUsername() {
        return mUsername;
    }


    /**
     * Sets the item username
     *
     * @param username
     *            username to set
     */
    public final void setUsername(String username) {
        mUsername = username;
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
     * Returns the item type
     */
    public String getSocial_type() {
        return mSocial_type;
    }

    /**
     * Sets the item type
     *
     * @param social_type
     *            phones to set
     */
    public final void setSocial_type(String social_type) {
        mSocial_type = social_type;
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
        return o instanceof SOCIAL && ((SOCIAL) o).mPerson_Id == mPerson_Id;
    }
}