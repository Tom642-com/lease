package com.itheima.lease.web.admin.vo.fee;


import com.itheima.lease.model.entity.FeeKey;
import com.itheima.lease.model.entity.FeeValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class FeeKeyVo extends FeeKey {

    @Schema(description = "杂费value列表")
    private List<FeeValue> feeValueList;
}
