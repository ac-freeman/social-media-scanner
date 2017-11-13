package com.acfreeman.socialmediascanner.AzureDB;


public class EMAILS {

    /**
     * Item phoneNums
     */
    @com.google.gson.annotations.SerializedName("email")
    private String mEmail;

    /**
     * Item Id
     */
    @com.google.gson.annotations.SerializedName("person_Id")
    private int mPerson_Id;
    
    /**
     * Item type
     */
    @com.google.gson.annotations.SerializedName("email_type")
    private String mEmail_type;

    /**
     * Indicates if the item is completed
     */
    @com.google.gson.annotations.SerializedName("complete")
    private boolean mComplete;


    /**
     * EMAILS constructor
     */
    public EMAILS() {

    }

    @Override
    public String toString() {
        return getEmail();
    }

    /**
     * Initializes a new EMAILS
     *
     * @param email
     *            The item phones
     * @param person_Id
     *            The item id
     * @param email_type
     *            The item type
     */
    public EMAILS(String email, String email_type, int person_Id) {
        this.setEmail(email);
        this.setPerson_Id(person_Id);
        this.setEmail_type(email_type);
    }

    /**
     * Returns the item phones
     */
    public String getEmail() {
        return mEmail;
    }


    /**
     * Sets the item email
     *
     * @param email
     *            email to set
     */
    public final void setEmail(String email) {
        mEmail = email;
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
    public String getEmail_type() {
        return mEmail_type;
    }

    /**
     * Sets the item type
     *
     * @param email_type
     *            phones to set
     */
    public final void setEmail_type(String email_type) {
        mEmail_type = email_type;
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
        return o instanceof EMAILS && ((EMAILS) o).mPerson_Id == mPerson_Id;
    }
}