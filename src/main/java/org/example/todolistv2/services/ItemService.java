package org.example.todolistv2.services;

import org.example.todolistv2.entity.Item;
import org.example.todolistv2.exceptions.BadRequestException;
import org.example.todolistv2.exceptions.NoAccessException;
import org.example.todolistv2.exceptions.NotFoundObjectException;
import org.example.todolistv2.exceptions.NotFoundOwnerException;
import org.example.todolistv2.mongotemplates.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
public class ItemService {
    private final ItemRepository itemRepository;
    private final GroupService groupService;

    @Autowired
    public ItemService(ItemRepository itemRepository, GroupService groupService){
        this.itemRepository = itemRepository;
        this.groupService = groupService;
    }

    public boolean create(String userId, String groupId, Item newItem) {
        if (groupService.notExist(groupId)) {
            throw new NotFoundOwnerException();
        }
        if (newItem == null) {
            throw new BadRequestException();
        }
        if (newItem.getGroupId() == null) {
            newItem.setGroupId(groupId);
        }
        if (!groupId.equals(newItem.getGroupId()) || newItem.getName() == null) {
            throw new BadRequestException();
        }
        if (newItem.getActivity() == null) {
            newItem.setActivity(true);
        }
        if (groupService.hasNotAccess(userId, groupId) || !newItem.getGroupId().equals(groupId)) {
            throw new NoAccessException();
        }
        itemRepository.insert(newItem);
        return true;
    }

    public boolean remove(String userId, String groupId, String itemId) {
        Item removingItem = itemRepository.findItemById(itemId);
        if (removingItem == null) {
            throw new NotFoundObjectException();
        }
        if (groupService.hasNotAccess(userId, groupId)
                || !groupId.equals(removingItem.getGroupId())) {
            throw new NoAccessException();
        }
        itemRepository.delete(removingItem);
        return true;
    }


    public List<Item> find() {
        List<Item> itemList = itemRepository.findAll();
        if (itemList.isEmpty()) {
            throw new NotFoundObjectException();
        }
        return itemList;
    }


    public List<Item> find(String userId, String groupId) {
        List<Item> itemList = itemRepository.findItemsByGroupId(groupId);
        if (itemList == null) {
            throw new NotFoundObjectException();
        }
        if (groupService.hasNotAccess(userId, groupId)) {
            throw new NoAccessException();
        }
        return itemList;
    }

    public Item getInfo(String userId, String groupId, String item_id) {
        if (groupService.notExist(groupId)) {
            throw new NotFoundOwnerException();
        }
        Item itemInfo = itemRepository.findItemById(item_id);
        if (itemInfo == null) {
            throw new NotFoundObjectException();
        }
        if (groupService.hasNotAccess(userId, groupId) || !groupId.equals(itemInfo.getGroupId())) {
            throw new NoAccessException();
        }
        return itemInfo;
    }

    public boolean update(String userId, String groupId, String itemId, Item updItem) {
        Item oldItem = itemRepository.findItemById(itemId);
        if (oldItem == null) {
            throw new NotFoundObjectException();
        }
        if (    updItem == null ||
                updItem.nullValues() ||
                (updItem.getId() != null && !updItem.getId().equals(oldItem.getId()))) {
            throw new BadRequestException();
        }
        if (groupService.hasNotAccess(userId, groupId)) {
            throw new NoAccessException();
        }
        if (updItem.getGroupId() != null && !groupId.equals(updItem.getGroupId())) {
            if (groupService.notExist(updItem.getGroupId())) {
                throw new NotFoundOwnerException();
            }
            oldItem.setGroupId(updItem.getGroupId());
        }
        if (updItem.getName() != null) {
            oldItem.setName(updItem.getName());
        }
        if (updItem.getActivity() != null) {
            oldItem.setActivity(updItem.getActivity());
        }

        itemRepository.save(oldItem);
        return true;
    }

    public boolean removeByGroupId(String groupId) {
        if (groupId == null) {
            throw new NullPointerException();
        }
        List<Item> removeItem = itemRepository.findItemsByGroupId(groupId);
        if (removeItem != null) {
            itemRepository.deleteAll(removeItem);
            return true;
        }
        return false;
    }
}