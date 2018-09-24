/*
 * Copyright 2018 Thomas Winkler
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

package utils.csvConverter;


public enum CSVFields {
    ORDERID(0),
    ARTICLE(2),
    DESCRIPTION(6),
    QUANTITY(9),
    TYPE_OF_CONTAINER(11),
    QUANTITY_PER_CONTAINER(13),
    PIECES_PER_CONTAINER(15),
    QUANTITY_OF_CONTAINER(17),
    SHORTAGE(21),
    MACHINE(25),
    LENGTH_OF_ARTICLE(27),
    PRINT_DESCRIPTION(31),
    PARTS_LIST(33),
    PARTS_LIST_DESCRIPTION(35),
    PARTS_LIST_QUANTITY(37),
    PARTS_LIST_UNIT(39),
    PROFILE_BODY(41),
    PROFILE_GASKET(43),
    PROFILE_GASKET_QUANTITY(45),
    TOOL(47),
    SCHUFO_O(49),
    SCHUFO_U(51);

    private int code;

    CSVFields(final int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}