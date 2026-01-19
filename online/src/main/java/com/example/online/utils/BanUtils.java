package com.example.online.utils;

import com.example.online.domain.model.ActionBan;
import com.example.online.domain.model.User;
import com.example.online.enumerate.BanType;
import com.example.online.exception.BadRequestException;
import com.example.online.exception.ForbiddenException;
import com.example.online.exception.ResourceNotFoundException;
import com.example.online.repository.ActionBanRepository;
import com.example.online.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class BanUtils {
    private final ActionBanRepository actionBanRepository;
    private final UserRepository userRepository;

    public void checkBan(Long userId, BanType banType, Long targetId){
        boolean isCheck = actionBanRepository
                .isBanned(userId, banType, targetId);
        if (isCheck){
            throw new ForbiddenException("You have been banned from this action");
        }
    }

    public boolean checkUserBan(Long userId, BanType banType, Long targetId){
        return actionBanRepository
                .isBanned(userId, banType, targetId);
    }

    @Transactional
    public void addBanRecord(Long userId, BanType banType, Long targetId){
        if (banType != BanType.EVERYTHING &&
                actionBanRepository.existsByUser_IdAndAction(userId, BanType.EVERYTHING)) {
            throw new ForbiddenException("User has been already banned from you");
        }

        ActionBan ban = actionBanRepository.findByUser_IdAndActionAndTargetId(userId, banType, targetId)
                .orElse(null);
        if (ban == null){
            User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
            actionBanRepository.save(ActionBan.builder().user(user).action(banType).targetId(targetId)
                    .bannedAt(LocalDateTime.now()).build()
            );
        }
    }

    public void addBanRecord(Long userId, BanType banType){
        if (banType == BanType.EVERYTHING) {
            List<ActionBan> smallBans = actionBanRepository.findAllByUser_IdAndActionNot(
                    userId,
                    BanType.EVERYTHING);

            actionBanRepository.deleteAll(smallBans);
        }

        ActionBan ban = actionBanRepository.findByUser_IdAndAction(userId, banType)
                .orElse(null);
        if (ban == null){
            User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
            actionBanRepository.save(ActionBan.builder().user(user).action(banType)
                    .bannedAt(LocalDateTime.now()).build()
            );
        }
    }

    public void removeBanRecord(Long userId, BanType banType, Long targetId){
        ActionBan ban = actionBanRepository
                .findByUser_IdAndActionAndTargetId(userId, banType, targetId).orElseThrow(() -> new BadRequestException("Bad request! Try again"));
        if (ban != null) {
            actionBanRepository.delete(ban);
        }
    }

    public void removeAllBanRecord(Long userId){
        for (var bantype : BanType.values()){
            actionBanRepository
                    .findByUser_IdAndAction(userId, bantype).ifPresent(actionBanRepository::delete);
        }
    }
}
