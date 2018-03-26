package me.goddragon.teaseai;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import me.goddragon.teaseai.api.chat.ChatHandler;
import me.goddragon.teaseai.api.chat.response.Response;
import me.goddragon.teaseai.api.chat.response.ResponseHandler;
import me.goddragon.teaseai.api.config.ConfigHandler;
import me.goddragon.teaseai.api.config.ConfigValue;
import me.goddragon.teaseai.api.scripts.ScriptHandler;
import me.goddragon.teaseai.api.scripts.personality.PersonalityManager;
import me.goddragon.teaseai.api.session.Session;
import me.goddragon.teaseai.gui.main.Controller;

/**
 * Created by GodDragon on 21.03.2018.
 */
public class TeaseAI extends Application {

    public static TeaseAI application;
    private ConfigHandler configHandler = new ConfigHandler("TeaseAI.properties");
    private Controller controller;
    private Thread mainThread;
    private Thread scriptThread;

    public final ConfigValue SUB_NAME = new ConfigValue("subName", "Sub", configHandler);
    public final ConfigValue DOM_NAME = new ConfigValue("domName", "Domme", configHandler);
    public final ConfigValue DOM_NAME_2 = new ConfigValue("domFriend1", "Friend 1", configHandler);
    public final ConfigValue DOM_NAME_3 = new ConfigValue("domFriend2", "Friend 2", configHandler);
    public final ConfigValue DOM_NAME_4 = new ConfigValue("domFriend3", "Friend 3", configHandler);

    public final ConfigValue PREFERED_SESSION_DURATION = new ConfigValue("preferredSessionDuration", "60", configHandler);

    private Session session;

    @Override
    public void start(Stage primaryStage) throws Exception {
        application = this;
        mainThread = Thread.currentThread();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("gui/main/main.fxml"));
        controller = new Controller();
        loader.setController(controller);
        Parent root = loader.load();
        primaryStage.setTitle("Tease AI");
        primaryStage.setScene(new Scene(root, 1480, 720));
        primaryStage.show();
        controller.initiate();

        //Load config values first
        configHandler.loadConfig();


        ChatHandler.getHandler().load();

        ScriptHandler.getHandler().load();
        PersonalityManager.getManager().loadPersonalities();

        this.session = new Session();

        scriptThread = new Thread() {
            @Override
            public void run() {
                /*ChatParticipant dom = ChatHandler.getHandler().getMainDomParticipant();
                VocabularyHandler.getHandler().registerVocabulary("tree", "%tree2%");
                VocabularyHandler.getHandler().registerVocabulary("tree2", "tree", "wood", "cool", "test");

                ResponseHandler.getHandler().registeResponse(new Response("fuck me", "lick me", "ShiT me") {
                    @Override
                    public boolean trigger() {
                        dom.sendMessage("Oh really?");
                        dom.sendMessage("Nice try!");
                        MediaHandler.getHandler().playVideo("D:\\Downloads\\I'm gonna get you off in 3 seconds.mp4", true);
                        dom.sendMessage("Done!");
                        return true;
                    }
                });

                Answer answer = dom.sendInput("Hey %tree%");
                while (true) {
                    System.out.println(answer.getAnswer());
                    if (answer.matchesRegexLowerCase("hey([ ]|$)", "hello([ ]|$)", "hi([ ]|$)")) {
                        break;
                    } else {
                        dom.sendMessage("What?");
                        answer.loop();
                    }
                }

                answer = dom.sendInput("How do you feel today?", 10);
                while (true) {
                    if (answer.isLike("Good", "You", "Great") || answer.isTimeout()) {
                        break;
                    } else {
                        dom.sendMessage("What?");
                        answer.loop();
                    }
                }*/

                synchronized (this) {
                    while(session.getActivePersonality() == null) {
                        try {
                            wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }

                session.start();
            }
        };

        scriptThread.start();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public boolean checkForNewResponses() {
        if(Thread.currentThread() != scriptThread) {
            throw new IllegalStateException("Can only check for new responses on the script thread");
        }

        Response queuedResponse = ResponseHandler.getHandler().getLatestQueuedReponse();
        if (queuedResponse != null) {
            ResponseHandler.getHandler().removeQueuedReponse(queuedResponse);
            return queuedResponse.trigger();
        }

        return false;
    }

    public void runOnUIThread(Runnable runnable) {
        //If we are not on the main thread, run it later on it
        if (Thread.currentThread() != mainThread) {
            Platform.runLater(runnable);
        } else {
            //We can safely run the runnable because we are on the main thread
            runnable.run();
        }
    }

    public void sleepScripThread(long sleepMillis) {
        sleepThread(scriptThread, sleepMillis);
    }

    public void waitScriptThread(long timeoutMillis) {
        waitThread(scriptThread, timeoutMillis);
    }

    public void waitThread(Thread thread) {
        waitThread(thread, 0);
    }

    public void waitThread(Thread thread, long timeoutMillis) {
        synchronized (thread) {
            try {
                thread.wait(timeoutMillis);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void sleepThread(Thread thread, long sleepMillis) {
        synchronized (thread) {
            try {
                thread.sleep(sleepMillis);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public Controller getController() {
        return controller;
    }

    public Thread getScriptThread() {
        return scriptThread;
    }

    public Thread getMainThread() {
        return mainThread;
    }

    public ConfigHandler getConfigHandler() {
        return configHandler;
    }

    public Session getSession() {
        return session;
    }
}
