package com.sounddesignz.rockdot.action.settings;

import com.intellij.openapi.project.Project;

import javax.swing.*;

/**
 * Created by ndoehring on 06.07.16.
 */
public class RdSettingsForm {
    private JPanel mainPanel;
    private JLabel myLabel;
    private JTextField titleTF;
    private JTextField descriptionTF;
    private JTextField authorTF;
    private JTextField copyrightTF;
    private JTextField colorTF;
    private JTextField fbidTF;
    private JTextField fbscopeTF;
    private JTextField fbshareurlTF;
    private JTextField fbdeeplinkTF;
    private JTextField gclientidTF;
    private JTextField gapikeyTF;
    private JTextField gscopeTF;
    private JTextField phpmyadminTF;
    private JTextField dbnameTF;
    private JTextField dbuserTF;
    private JTextField dbpassTF;
    private JTextField dbhostTF;
    private Project _project;
    private RdProperties p;

    public RdSettingsForm(Project project, RdProperties rdProperties){
        _project = project;
        this.p = rdProperties;
        myLabel.setText(myLabel.getText() + " World.");

        titleTF.setText(p.getProperty("project.title"));
        descriptionTF.setText(p.getProperty("project.description"));
        authorTF.setText(p.getProperty("project.author"));
        copyrightTF.setText(p.getProperty("project.copyright"));
        colorTF.setText(p.getProperty("project.bgcolor"));

        if(p.getProperty("project.facebook.appid") != null){
            fbidTF.setText(p.getProperty("project.facebook.appid"));
            fbscopeTF.setText(p.getProperty("project.facebook.scope"));
            fbshareurlTF.setText(p.getProperty("project.url.content"));
            fbdeeplinkTF.setText(p.getProperty("project.url.content.deeplink"));
        }
        else{
            fbidTF.setEnabled(false);
            fbscopeTF.setEnabled(false);
            fbshareurlTF.setEnabled(false);
            fbdeeplinkTF.setEnabled(false);
        }

        if(p.getProperty("project.google.oauth.clientid") != null) {
            gclientidTF.setText(p.getProperty("project.google.oauth.clientid"));
            gapikeyTF.setText(p.getProperty("project.google.apikey"));
            gscopeTF.setText(p.getProperty("project.google.scope.plus"));
        }
        else{
            gclientidTF.setEnabled(false);
            gapikeyTF.setEnabled(false);
            gscopeTF.setEnabled(false);
        }

        if(p.getProperty("accounts.db.name") != null) {
            phpmyadminTF.setText(p.getProperty("accounts.phpmyadmin"));
            dbnameTF.setText(p.getProperty("accounts.db.name"));
            dbuserTF.setText(p.getProperty("accounts.db.user"));
            dbpassTF.setText(p.getProperty("accounts.db.pass"));
            dbhostTF.setText(p.getProperty("accounts.db.host"));
        }
        else{
            phpmyadminTF.setEnabled(false);
            dbnameTF.setEnabled(false);
            dbuserTF.setEnabled(false);
            dbpassTF.setEnabled(false);
            dbhostTF.setEnabled(false);
        }





    }

    public JComponent getContents() {
        return mainPanel;
    }

    public void save() {
        p.setProperty("project.title", titleTF.getText());
        p.setProperty("project.description", descriptionTF.getText());
        p.setProperty("project.author", authorTF.getText());
        p.setProperty("project.copyright", copyrightTF.getText());
        p.setProperty("project.bgcolor", colorTF.getText());

        p.setProperty("project.facebook.appid", fbidTF.getText());
        p.setProperty("project.facebook.scope", fbscopeTF.getText());
        p.setProperty("project.url.content", fbshareurlTF.getText());
        p.setProperty("project.url.content.deeplink", fbdeeplinkTF.getText());

        p.setProperty("project.google.oauth.clientid", gclientidTF.getText());
        p.setProperty("project.google.apikey", gapikeyTF.getText());
        p.setProperty("project.google.scope.plus", gscopeTF.getText());

        p.setProperty("accounts.phpmyadmin", phpmyadminTF.getText());
        p.setProperty("accounts.db.name", dbnameTF.getText());
        p.setProperty("accounts.db.user", dbuserTF.getText());
        p.setProperty("accounts.db.pass", dbpassTF.getText());
        p.setProperty("accounts.db.host", dbhostTF.getText());

        p.save();
    }
}

          /*

                project.title
                project.description
                project.author
                project.copyright
                project.bgcolor


                project.facebook.appid
                project.facebook.scopes
                ugc:
                project.url.content
                project.url.content.deeplink

                project.google.oauth.clientid
                project.google.apikey
                project.google.scope.plus

                accounts.phpmyadmin   = http://somehost
                accounts.db.user      = root
                accounts.db.name      = rd_framework
                accounts.db.pass      = root
                accounts.db.host      = localhost

                ## SQL table prefix  ##
                accounts.dbtableprefix  = {{projectName}}

                ## Zend Config  ##
                zend.mode = production
                zend.debug = false
                project.host = www.local
                zend.admin.ip = 127.0.0.1
                zend.crypt.key = (((!XY\u497AO$%4746/JA-;JN-%E&363=IB-J#ALoXiIhjk89dSagNN640523)))
                zend.crypt.vector = ==&%?!91
                zend.session.name = ROCKDOT_SESS
                zend.ugcmanager.user = rockdot
                zend.ugcmanager.pass = d8947d975826d6f47579ac717356f8f6

                 */

