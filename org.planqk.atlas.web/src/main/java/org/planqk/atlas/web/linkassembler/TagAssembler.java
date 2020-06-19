package org.planqk.atlas.web.linkassembler;
// Tags will be used/tested and included in the future

//@Component
//public class TagAssembler extends GenericLinkAssembler<TagDto> {

//    @Override
//    public void addLinks(EntityModel<TagDto> resource) {
//        resource.add(links.linkTo(methodOn(TagController.class).getTagById(getId(resource))).withSelfRel());
//        resource.add(links.linkTo(methodOn(TagController.class).getAlgorithmsOfTag(getId(resource)))
//                .withRel(Constants.ALGORITHMS));
//        resource.add(links.linkTo(methodOn(TagController.class).getImplementationsOfTag(getId(resource)))
//                .withRel(Constants.IMPLEMENTATIONS));
//    }
//
//    public void addAlgorithmLink(CollectionModel<EntityModel<AlgorithmDto>> resources, UUID id) {
//        resources.add(links.linkTo(methodOn(TagController.class).getAlgorithmsOfTag(id)).withSelfRel());
//    }
//
//    public void addImplementationLink(CollectionModel<EntityModel<ImplementationDto>> resources, UUID id) {
//        resources.add(links.linkTo(methodOn(TagController.class).getImplementationsOfTag(id)).withSelfRel());
//    }
//
//    private UUID getId(EntityModel<TagDto> resource) {
//        return resource.getContent().getId();
//    }
//}
