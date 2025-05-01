module project {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires java.sql;

    requires com.google.zxing;
    requires com.google.zxing.javase;
    requires java.desktop; // For java.awt.image
    requires javafx.swing; // For javafx.embed.swing

    requires java.net.http;
    requires org.json;
    requires flying.saucer.pdf;
    requires stripe.java;
    requires jdk.jsobject;


    exports project;
    exports project.interfaces;
    exports project.models;
    exports project.service;
    exports project.utils;

    exports project.controllers;
    opens project.controllers to javafx.fxml;
    opens project to javafx.fxml;


}