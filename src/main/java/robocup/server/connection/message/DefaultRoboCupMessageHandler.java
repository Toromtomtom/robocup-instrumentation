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

package robocup.server.connection.message;

import org.apache.log4j.Logger;

/**
 * Created by tw250
 */
public class DefaultRoboCupMessageHandler implements IRoboCupMessageHandler {

    private static final Logger log =
            Logger.getLogger(DefaultRoboCupMessageHandler.class);

    @Override
    public void handleMessage(RoboCupShowMessage message) {

        log.info("Received message " + message);

    }
}
