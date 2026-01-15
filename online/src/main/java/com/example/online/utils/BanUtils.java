package com.example.online.utils;

import com.example.online.domain.model.ActionBan;
import com.example.online.domain.model.User;
import com.example.online.enumerate.BanType;
import com.example.online.exception.ForbiddenException;
import com.example.online.exception.ResourceNotFoundException;
import com.example.online.repository.ActionBanRepository;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public class BanUtils {
    private final ActionBanRepository actionBanRepository;
    public void checkBan(User user, BanType banType, Long targetId){
        boolean isCheck = actionBanRepository
                .existsByUser_IdAndActionInAndTargetId(user.getId(), List.of(banType, BanType.EVERYTHING), targetId);
        if (!isCheck){
            throw new ForbiddenException("You have been banned from this action");
        }
    }

    public void addBanRecord(User user, BanType banType, Long targetId){
        if (banType == BanType.EVERYTHING) {
            List<ActionBan> smallBans = actionBanRepository.findAllByUser_IdAndBanTypeNot(
                            user.getId(),
                            BanType.EVERYTHING);

            actionBanRepository.deleteAll(smallBans);
        }

        if (banType != BanType.EVERYTHING &&
                actionBanRepository.existsByUser_IdAndBanType(user.getId(), BanType.EVERYTHING)) {
            throw new ForbiddenException("User has been already banned from everything");
        }

        ActionBan ban = actionBanRepository.findByUser_IdAndBanTypeAndTargetId(user.getId(), banType, targetId)
                .orElse(null);
        if (ban == null){
            actionBanRepository.save(ActionBan.builder().user(user).action(banType).targetId(targetId)
                    .bannedAt(LocalDateTime.now()).build()
            );
        }
    }

    public void removeBanRecord(Long userId, BanType banType, Long targetId){
        ActionBan ban = actionBanRepository
                .findByUser_IdAndBanTypeAndTargetId(userId, banType, targetId).orElseThrow(() -> new ResourceNotFoundException("Ban not found"));
        actionBanRepository.delete(ban);
    }

    public void removeAllBanRecord(Long userId){
        ActionBan ban = actionBanRepository
                .findByUser_IdAndBanType(userId, BanType.EVERYTHING).orElseThrow(() -> new ResourceNotFoundException("Ban not found"));
        actionBanRepository.delete(ban);
    }
}
