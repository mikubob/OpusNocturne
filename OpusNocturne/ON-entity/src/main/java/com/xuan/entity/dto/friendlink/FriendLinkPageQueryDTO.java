package com.xuan.entity.dto.friendlink;

import com.xuan.common.domain.BasePageQueryDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 友链分页查询参数
 *
 * @author 玄〤
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "友链分页查询参数")
public class FriendLinkPageQueryDTO extends BasePageQueryDTO {

    @Schema(description = "状态：0-待审核；1-上线；2-下架")
    private Integer status;

    @Schema(description = "名称搜索")
    private String name;
}
