package com.momo.savanger.api.transfer;

import com.momo.savanger.api.transfer.dto.CreateTransferDto;
import com.momo.savanger.api.transfer.dto.TransferFullDto;
import com.momo.savanger.api.transfer.dto.TransferSimpleDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TransferMapper {

    Transfer toTransfer(CreateTransferDto createTransferDto);

    TransferSimpleDto toTransferSimpleDto(Transfer transfer);

    TransferFullDto toTransferFullDto(Transfer transfer);
}
