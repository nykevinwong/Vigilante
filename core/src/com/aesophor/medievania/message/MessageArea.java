package com.aesophor.medievania.message;

import com.aesophor.medievania.manager.GameStateManager;
import com.aesophor.medievania.util.Constants;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class MessageArea extends Stage {

    private GameStateManager gsm;
    private int messageQueueSize;
    private float messageLifetime;

    public MessageArea(GameStateManager gsm, int messageQueueSize, float messageLifetime) {
        super(new FitViewport(Constants.V_WIDTH, Constants.V_HEIGHT), gsm.getBatch());
        this.gsm = gsm;

        this.messageQueueSize = messageQueueSize;
        this.messageLifetime = messageLifetime;
    }

    public void show(String content) {
        if (getActors().size > messageQueueSize) {
            getActors().get(0).remove();
        }

        // Move previous messages up.
        for (Actor message : getActors()) {
            ((Message) message).addAction(Actions.moveBy(0, 10f, .2f));
        }

        // Display the new message.
        Message newMsg = new Message(content, new Label.LabelStyle(gsm.getFont().getDefaultFont(), Color.WHITE), messageLifetime);
        newMsg.setPosition(10f, 0f);
        newMsg.addAction(Actions.moveBy(0f, 10f, .2f));
        addActor(newMsg);
    }


    public void update(float delta) {
        // If any previous message has expired
        for (Actor message : getActors()) {
            Message m = ((Message) message);
            m.update(delta);

            if (m.hasExpired()) {
                m.addAction(Actions.sequence(Actions.fadeOut(1f), Actions.removeActor()));
            }
        }

        act(delta);
    }

}
