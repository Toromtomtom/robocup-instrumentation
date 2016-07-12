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

package robocup.server.connection.message.parser;

import org.junit.Test;
import robocup.server.connection.message.RoboCupMessage;

import java.io.IOException;
import java.util.Scanner;

public class RoboCupParserTest {

    @Test
    public void TestAntlr() throws IOException {

        String show =
                new Scanner(getClass().getResourceAsStream("/example" + "" +
                        ".message"),
                        "UTF-8").useDelimiter("\\A").next();

        String msg = "(msg 0 1 \"(change_player_type l 1 1)\")";

        RoboCupMessage m1 = RoboCupParser.parseMessage(show);
        RoboCupMessage m2 = RoboCupParser.parseMessage(msg);

        System.out.println(m1);
        System.out.println(m2);
    }

}