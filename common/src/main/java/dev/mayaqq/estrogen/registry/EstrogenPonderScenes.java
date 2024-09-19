package dev.mayaqq.estrogen.registry;

import com.simibubi.create.foundation.ponder.PonderRegistrationHelper;
import com.simibubi.create.infrastructure.ponder.AllPonderTags;
import dev.mayaqq.estrogen.Estrogen;
import dev.mayaqq.estrogen.integrations.newage.CreateNewAgeCompat;
import dev.mayaqq.estrogen.registry.ponders.CentrifugeStoryboard;
import earth.terrarium.botarium.util.CommonHooks;

public class EstrogenPonderScenes {

    static final PonderRegistrationHelper HELPER = new PonderRegistrationHelper(Estrogen.MOD_ID);

    public static void register() {
        HELPER.addStoryBoard(EstrogenBlocks.CENTRIFUGE.getId(), Estrogen.id("centrifuge/intro"), new CentrifugeStoryboard.Intro(), AllPonderTags.KINETIC_APPLIANCES);
        HELPER.addStoryBoard(EstrogenBlocks.CENTRIFUGE.getId(), Estrogen.id("centrifuge/basic"), new CentrifugeStoryboard.Basic(), AllPonderTags.KINETIC_APPLIANCES);
        if (CommonHooks.isModLoaded("create_new_age")) CreateNewAgeCompat.registerPonderScenes(HELPER);
    }
}
