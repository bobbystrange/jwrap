package org.dreamcat.jwrap.excel.map.serializable;

import java.util.List;
import lombok.Data;

/**
 * Create by tuke on 2021/2/4
 */
@Data
public class SerializablePiece {

    private List<SerializablePieceHead> head;
    private List<List<String>> body;
}
