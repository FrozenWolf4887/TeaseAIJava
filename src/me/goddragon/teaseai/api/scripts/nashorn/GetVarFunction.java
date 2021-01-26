package me.goddragon.teaseai.api.scripts.nashorn;

import me.goddragon.teaseai.TeaseAI;
import me.goddragon.teaseai.api.config.VariableHandler;
import me.goddragon.teaseai.api.scripts.personality.Personality;
import me.goddragon.teaseai.api.scripts.personality.PersonalityManager;
import me.goddragon.teaseai.api.session.Session;

/**
 * Created by GodDragon on 25.03.2018.
 */
public class GetVarFunction extends CustomFunctionExtended {
    public GetVarFunction() {
        super("getVar", "checkVar", "getDate", "getVariable");
    }

    @Override
    public boolean isFunction() {
        return true;
    }

    @Override
    protected void preOnCall() {
        final Session session = TeaseAI.application.getSession();
        Personality personality;
        if (session == null) {
            personality = PersonalityManager.getManager().getLoadingPersonality();
        } else {
            personality = session.getActivePersonality();
        }

        variableHandler = personality.getVariableHandler();
    }

    protected Object onCall(String variableName) {
        return variableHandler.getVariableValue(variableName);
    }

    protected Object onCall(String variableName, Object defaultValue) {
        if (variableHandler.variableExist(variableName)) {
            return variableHandler.getVariableValue(variableName);
        } else {
            return defaultValue;
        }
    }

    private VariableHandler variableHandler;
}
