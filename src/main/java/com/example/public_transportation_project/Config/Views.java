package com.example.public_transportation_project.Config;

public class Views {
    // שדות שכולם יכולים לראות (שם, תפקיד וכו')
    public interface Public {}

    // שדות שרק מנהל יכול לראות (טלפון, אימייל, סטטוס מחיקה)
    public interface Admin extends Public {}
}