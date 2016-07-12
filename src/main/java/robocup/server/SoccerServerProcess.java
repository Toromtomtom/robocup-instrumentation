/*
 * Copyright 2016 Tom Warnke
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package robocup.server;

import org.apache.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A class to configure and start the binaries for the soccer server and the teams. Extremely platform (Linux)
 * dependent!
 *
 * @author Tom Warnke
 */
class SoccerServerProcess {

    private static final Logger log = Logger.getLogger(SoccerServerProcess
            .class);

    private static final String SERVER_DIRECTORY = "robocup/rcssserver";

    private static final String SERVER_EXECUTABLE = "bin/rcssserver";

    private static final String LIBRARY_DIRECTORY = "lib64/"; //TODO

    private static final String LEFT_TEAM_EXECUTABLE = "team_l.sh";

    private static final String RIGHT_TEAM_EXECUTABLE = "team_r.sh";

    /**
     * Executes the server binary. See documentation of the soccer server for details on the parameters.
     *
     * @param matchDirectoryPath
     * @param autoMode
     * @throws IOException
     */
    public SoccerServerProcess(String matchDirectoryPath, boolean autoMode)
            throws IOException {

        File matchDirectory = checkOrCreateDirectory(matchDirectoryPath);

        Process serverProcess = buildServerProcess(matchDirectory, autoMode);

        addShutDownHook(serverProcess);
    }

    private Process buildServerProcess(File matchDirectory, boolean autoMode)
            throws IOException {
        List<String> command = new ArrayList<>();
        command.add(SERVER_EXECUTABLE);
        // soccer server manages teams:
        command.add("server::team_l_start=../" + LEFT_TEAM_EXECUTABLE);
        command.add("server::team_r_start=../" + RIGHT_TEAM_EXECUTABLE);
        command.add("server::kick_off_wait=150"); // server waits 15 seconds
        if (autoMode) {
            command.add("server::auto_mode=on");
        }
        command.add("server::game_over_wait=0");
        command.add("server::game_log_dir=" + matchDirectory.getAbsolutePath());
        command.add("server::text_log_dir=" + matchDirectory.getAbsolutePath());

        ProcessBuilder builder = new ProcessBuilder();
        builder.environment().put("LD_LIBRARY_PATH", LIBRARY_DIRECTORY);
        builder.command(command);
        builder.directory(new File(SERVER_DIRECTORY));
        builder.redirectErrorStream(true);
        Process process = builder.start();
        redirectStream(process.getInputStream());

        return process;
    }

    /**
     * Check if the given directory exists and if not, try to create it
     *
     * @param directoryPath
     * @throws IOException
     */
    private File checkOrCreateDirectory(String directoryPath) throws
            IOException {
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            // directory does not exist -> create it
            if (!directory.mkdirs()) {
                throw new IOException("Match directory " + directory
                        .getCanonicalPath()
                        + " could not be created!");
            }
        } else {
            // directory exists
            if (!directory.isDirectory()) {
                throw new IOException("Match directory " + directory
                        .getCanonicalPath()
                        + " is not a directory!");
            }
        }
        return directory;
    }

    /**
     * Redirect the given output stream to System.out (in a new thread)
     *
     * @param stream
     */
    private void redirectStream(final InputStream stream) {

        Runnable streamReader = () -> {
            BufferedReader errorReader =
                    new BufferedReader(new InputStreamReader(stream));
            String line;
            try {
                while ((line = errorReader.readLine()) != null) {
                    log.info("Soccer Server: " + line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        };

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(streamReader);
        executor.shutdown();
    }

    /**
     * Kill server process when java is terminated (red button in eclipse)
     *
     * @param serverProcess
     */
    private void addShutDownHook(Process serverProcess) {

        Runnable shutDownHook = serverProcess::destroy;

        Runtime.getRuntime().addShutdownHook(new Thread(shutDownHook));
    }

}
