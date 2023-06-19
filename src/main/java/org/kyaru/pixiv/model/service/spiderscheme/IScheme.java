package org.kyaru.pixiv.model.service.spiderscheme;

import org.kyaru.pixiv.model.utils.requester.RequestClient;

import java.util.List;

public interface IScheme {
    List<String> getArtworkIDs(RequestClient requestClient, int sourceLimit);
    String getFileName(RequestClient requestClient);
}


