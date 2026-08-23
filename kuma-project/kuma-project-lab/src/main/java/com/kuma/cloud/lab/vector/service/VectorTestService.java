package com.kuma.cloud.lab.vector.service;

import com.kuma.cloud.lab.vector.domain.dto.VectorSearchDTO;
import com.kuma.cloud.lab.vector.domain.dto.VectorUpsertDTO;
import com.kuma.cloud.lab.vector.domain.vo.VectorDocumentVO;
import com.kuma.cloud.lab.vector.domain.vo.VectorMatchVO;
import com.kuma.cloud.lab.vector.domain.vo.VectorScenarioVO;
import com.kuma.cloud.lab.vector.domain.vo.VectorStoreStatusVO;

import java.util.List;

public interface VectorTestService {

    VectorScenarioVO runScenario();

    VectorDocumentVO upsert(VectorUpsertDTO dto);

    List<VectorMatchVO> search(VectorSearchDTO dto);

    long delete(String id);

    long resetCollection();

    VectorStoreStatusVO status();

}
