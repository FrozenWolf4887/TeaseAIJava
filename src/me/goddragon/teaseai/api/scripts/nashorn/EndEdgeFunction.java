package me.goddragon.teaseai.api.scripts.nashorn;

import me.goddragon.teaseai.api.session.StrokeHandler;
import me.goddragon.teaseai.utils.TeaseLogger;

import java.util.Arrays;
import java.util.logging.Level;

/**
 * Created by GodDragon on 09.04.2018.
 */
public class EndEdgeFunction extends CustomFunction {

    public EndEdgeFunction() {
        super("endEdge", "stopEdge", "endEdging", "stopEdging");
    }

    @Override
    public boolean isFunction() {
        return true;
    }

    @Override
    public Object call(Object object, Object... args) {
        switch (args.length) {
            case 0:
                if(!StrokeHandler.getHandler().isEdging() && !StrokeHandler.getHandler().isOnEdge()) {
                    TeaseLogger.getLogger().log(Level.SEVERE, "Sub was not on the edge however " + getFunctionName() + " was called.");
                } else {
                    StrokeHandler.getHandler().setEdging(false);
                    StrokeHandler.getHandler().setOnEdge(false);
                }

                return null;
        }

        TeaseLogger.getLogger().log(Level.SEVERE, getFunctionName() + " called with invalid args:" + Arrays.asList(args).toString());
        return null;
    }
}
