package com.sample.shop.service;

import com.sample.shop.domain.WishList;
import com.sample.shop.repository.WishListRepository;
import com.sample.shop.repository.search.WishListSearchRepository;
import com.sample.shop.service.dto.WishListDTO;
import com.sample.shop.service.mapper.WishListMapper;
import com.sample.shop.service.projections.mapper.WishListProjectionMapper;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

/**
 * Service Implementation for managing {@link WishList}.
 */
@Service
@Transactional
public class WishListService {

    private final Logger log = LoggerFactory.getLogger(WishListService.class);

    private final WishListRepository wishListRepository;

    private final WishListMapper wishListMapper;

    private final WishListProjectionMapper wishListProjectionMapper;

    private final WishListSearchRepository wishListSearchRepository;

    private final EntityManager entityManager;
    private final SearchSession searchSession;


    public WishListService(
        WishListRepository wishListRepository,
        WishListMapper wishListMapper,
        WishListProjectionMapper wishListProjectionMapper, WishListSearchRepository wishListSearchRepository,
        EntityManager entityManager
    ) {
        this.wishListRepository = wishListRepository;
        this.wishListMapper = wishListMapper;
        this.wishListProjectionMapper = wishListProjectionMapper;
        this.wishListSearchRepository = wishListSearchRepository;
        this.entityManager = entityManager;
        searchSession = Search.session(entityManager);
    }

    /**
     * Save a wishList.
     *
     * @param wishListDTO the entity to save.
     * @return the persisted entity.
     */
    public WishListDTO save(WishListDTO wishListDTO) {
        log.debug("Request to save WishList : {}", wishListDTO);
        WishList wishList = wishListMapper.toEntity(wishListDTO);
        wishList = wishListRepository.save(wishList);
        WishListDTO result = wishListMapper.toDto(wishList);
        //wishListSearchRepository.index(wishList);
        return result;
    }

    /**
     * Update a wishList.
     *
     * @param wishListDTO the entity to save.
     * @return the persisted entity.
     */
    public WishListDTO update(WishListDTO wishListDTO) {
        log.debug("Request to update WishList : {}", wishListDTO);
        WishList wishList = wishListMapper.toEntity(wishListDTO);
        wishList = wishListRepository.save(wishList);
        WishListDTO result = wishListMapper.toDto(wishList);
        //wishListSearchRepository.index(wishList);
        return result;
    }

    /**
     * Partially update a wishList.
     *
     * @param wishListDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<WishListDTO> partialUpdate(WishListDTO wishListDTO) {
        log.debug("Request to partially update WishList : {}", wishListDTO);

        return wishListRepository
            .findById(wishListDTO.getId())
            .map(existingWishList -> {
                wishListMapper.partialUpdate(existingWishList, wishListDTO);

                return existingWishList;
            })
            .map(wishListRepository::save)
            .map(savedWishList -> {
                //wishListSearchRepository.save(savedWishList);

                return savedWishList;
            })
            .map(wishListMapper::toDto);
    }

    /**
     * Get all the wishLists.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<WishListDTO> findAll() {
        log.debug("Request to get all WishLists");
        return wishListRepository.findAll().stream().map(wishListMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one wishList by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<WishListDTO> findOne(UUID id) {
        log.debug("Request to get WishList : {}", id);
        return wishListRepository.findById(id).map(wishListMapper::toDto);
    }

    /**
     * Delete the wishList by id.
     *
     * @param id the id of the entity.
     */
    public void delete(UUID id) {
        log.debug("Request to delete WishList : {}", id);
        wishListRepository.deleteById(id);
        //wishListSearchRepository.deleteById(id);
    }

    /**
     * Search for the wishList corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<WishListDTO> search(String query) {
        log.debug("Request to search WishLists for query {}", query);

        List<WishListDTO> wishes = searchSession.search( WishList.class )
            .where( (f, b) -> {
                    b.must(f.matchAll());
                }
            ).fetchHits( 20 ).stream().map(wishListMapper::toDto).collect(Collectors.toList());
        return StreamSupport
            .stream(wishListSearchRepository.search(query).spliterator(), false)
            .map(wishListMapper::toDto)
            .collect(Collectors.toList());
    }

    /**
     * Search for the wishList corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<WishListDTO> searchWithProjection(String query) {
        log.debug("Request to search WishLists for query {}", query);

        List<WishListDTO> wishes = searchSession.search( WishList.class )
            .select(com.sample.shop.service.projections.dto.WishList.class)
            .where( (f, b) -> {
                    b.must(f.matchAll());
                }
            ).fetchHits( 20 ).stream().map(wishListProjectionMapper::toDto).collect(Collectors.toList());
        return StreamSupport
            .stream(wishListSearchRepository.search(query).spliterator(), false)
            .map(wishListMapper::toDto)
            .collect(Collectors.toList());
    }
}
