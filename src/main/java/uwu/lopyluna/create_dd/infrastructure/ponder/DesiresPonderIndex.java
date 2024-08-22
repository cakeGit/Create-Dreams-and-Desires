package uwu.lopyluna.create_dd.infrastructure.ponder;

import com.simibubi.create.foundation.ponder.PonderRegistrationHelper;
import com.simibubi.create.foundation.ponder.PonderScene;
import com.simibubi.create.infrastructure.config.AllConfigs;
import uwu.lopyluna.create_dd.DesiresCreate;
import uwu.lopyluna.create_dd.infrastructure.ponder.scenes.CogCrankScenes;
import uwu.lopyluna.create_dd.registry.DesiresBlocks;

@SuppressWarnings("unused")
public class DesiresPonderIndex {
    
    static final PonderRegistrationHelper HELPER = new PonderRegistrationHelper(DesiresCreate.MOD_ID);
    
    public static final boolean REGISTER_DEBUG_SCENES = false;
    
    public static void register() {
        HELPER.forComponents(DesiresBlocks.COG_CRANK, DesiresBlocks.LARGE_COG_CRANK)
            .addStoryBoard("kinetics/cog_crank", CogCrankScenes::cogCrank);
    }
    
    public static boolean editingModeActive() {
        return AllConfigs.client().editingMode.get();
    }
    
}
