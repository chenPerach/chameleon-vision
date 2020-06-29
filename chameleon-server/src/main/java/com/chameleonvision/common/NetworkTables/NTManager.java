package com.chameleonvision.common.NetworkTables;

import com.chameleonvision.common.configuration.ConfigManager;
import com.chameleonvision.common.logging.LogGroup;
import com.chameleonvision.common.logging.Logger;
import com.chameleonvision.common.scripting.ScriptEventType;
import com.chameleonvision.common.scripting.ScriptManager;
import edu.wpi.first.networktables.LogMessage;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import java.util.function.Consumer;

public class NTManager {

    private static final Logger logger = new Logger(NTManager.class, LogGroup.General);

    private static final NetworkTableInstance ntInstance = NetworkTableInstance.getDefault();

    public static final String TableName = "/chameleon-vision/";
    public static final NetworkTable kRootTable =
            NetworkTableInstance.getDefault().getTable(TableName);

    public static boolean isServer = false;

    private static int getTeamNumber() {
        return ConfigManager.getInstance().getConfig().getNetworkConfig().teamNumber;
    }

    public static void setClientMode(String host) {
        isServer = false;
        logger.info("Starting NT Client");
        ntInstance.stopServer();
        if (host != null) {
            ntInstance.startClient(host);
        } else {
            ntInstance.startClientTeam(getTeamNumber());
            if (ntInstance.isConnected()) {
                logger.info("[NetworkTablesManager] Connected to the robot!");
            } else {
                logger.info(
                        "[NetworkTablesManager] Could NOT to the robot! Will retry in the background...");
            }
        }
    }

    public static void setTeamClientMode() {
        setClientMode(null);
    }

    public static void setServerMode() {
        isServer = true;
        logger.info("Starting NT Server");
        ntInstance.stopClient();
        ntInstance.startServer();
    }

    private static class NTLogger implements Consumer<LogMessage> {

        private boolean hasReportedConnectionFailure = false;

        @Override
        public void accept(LogMessage logMessage) {
            if (!hasReportedConnectionFailure && logMessage.message.contains("timed out")) {
                logger.error("NT Connection has failed! Will retry in background.");
                hasReportedConnectionFailure = true;
            } else if (logMessage.message.contains("connected")) {
                logger.info("NT Connected!");
                hasReportedConnectionFailure = false;
                ScriptManager.queueEvent(ScriptEventType.kNTConnected);
            }
        }
    }

    static {
        NetworkTableInstance.getDefault().addLogger(new NTLogger(), 0, 255); // to hide error messages
    }
}
