package com.railtrack.ai.service;

import com.railtrack.ai.dto.AiChatResponse;
import com.railtrack.pnr.dto.response.PnrResponse;

public interface AiService {

    AiChatResponse chat(String message);

    String explainPnr(PnrResponse response);
}