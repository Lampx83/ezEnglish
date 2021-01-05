
package flashcard;

import java.util.List;

import flashcard.Creator;
import flashcard.Terms;

public class Sets{
   	private Number access_type;
   	private Added_by added_by;
   	private Number added_timestamp;
   	private boolean can_edit;
   	private List class_ids;
   	private String created_by;
   	private Number created_date;
   	private Creator creator;
   	private Number creator_id;
   	private String description;
   	private String display_timestamp;
   	private String editable;
   	private boolean has_access;
   	private boolean has_discussion;
   	private boolean has_images;
   	private Number id;
   	private String lang_definitions;
   	private String lang_terms;
   	private Number modified_date;
   	private Number password_edit;
   	private Number password_use;
   	private Number published_date;
   	private List subjects;
   	private Number term_count;
   	private List<Terms> terms;
   	private String title;
   	private String url;
   	private String visibility;

 	public Number getAccess_type(){
		return this.access_type;
	}
	public void setAccess_type(Number access_type){
		this.access_type = access_type;
	}
 	public Added_by getAdded_by(){
		return this.added_by;
	}
	public void setAdded_by(Added_by added_by){
		this.added_by = added_by;
	}
 	public Number getAdded_timestamp(){
		return this.added_timestamp;
	}
	public void setAdded_timestamp(Number added_timestamp){
		this.added_timestamp = added_timestamp;
	}
 	public boolean getCan_edit(){
		return this.can_edit;
	}
	public void setCan_edit(boolean can_edit){
		this.can_edit = can_edit;
	}
 	public List getClass_ids(){
		return this.class_ids;
	}
	public void setClass_ids(List class_ids){
		this.class_ids = class_ids;
	}
 	public String getCreated_by(){
		return this.created_by;
	}
	public void setCreated_by(String created_by){
		this.created_by = created_by;
	}
 	public Number getCreated_date(){
		return this.created_date;
	}
	public void setCreated_date(Number created_date){
		this.created_date = created_date;
	}
 	public Creator getCreator(){
		return this.creator;
	}
	public void setCreator(Creator creator){
		this.creator = creator;
	}
 	public Number getCreator_id(){
		return this.creator_id;
	}
	public void setCreator_id(Number creator_id){
		this.creator_id = creator_id;
	}
 	public String getDescription(){
		return this.description;
	}
	public void setDescription(String description){
		this.description = description;
	}
 	public String getDisplay_timestamp(){
		return this.display_timestamp;
	}
	public void setDisplay_timestamp(String display_timestamp){
		this.display_timestamp = display_timestamp;
	}
 	public String getEditable(){
		return this.editable;
	}
	public void setEditable(String editable){
		this.editable = editable;
	}
 	public boolean getHas_access(){
		return this.has_access;
	}
	public void setHas_access(boolean has_access){
		this.has_access = has_access;
	}
 	public boolean getHas_discussion(){
		return this.has_discussion;
	}
	public void setHas_discussion(boolean has_discussion){
		this.has_discussion = has_discussion;
	}
 	public boolean getHas_images(){
		return this.has_images;
	}
	public void setHas_images(boolean has_images){
		this.has_images = has_images;
	}
 	public Number getId(){
		return this.id;
	}
	public void setId(Number id){
		this.id = id;
	}
 	public String getLang_definitions(){
		return this.lang_definitions;
	}
	public void setLang_definitions(String lang_definitions){
		this.lang_definitions = lang_definitions;
	}
 	public String getLang_terms(){
		return this.lang_terms;
	}
	public void setLang_terms(String lang_terms){
		this.lang_terms = lang_terms;
	}
 	public Number getModified_date(){
		return this.modified_date;
	}
	public void setModified_date(Number modified_date){
		this.modified_date = modified_date;
	}
 	public Number getPassword_edit(){
		return this.password_edit;
	}
	public void setPassword_edit(Number password_edit){
		this.password_edit = password_edit;
	}
 	public Number getPassword_use(){
		return this.password_use;
	}
	public void setPassword_use(Number password_use){
		this.password_use = password_use;
	}
 	public Number getPublished_date(){
		return this.published_date;
	}
	public void setPublished_date(Number published_date){
		this.published_date = published_date;
	}
 	public List getSubjects(){
		return this.subjects;
	}
	public void setSubjects(List subjects){
		this.subjects = subjects;
	}
 	public Number getTerm_count(){
		return this.term_count;
	}
	public void setTerm_count(Number term_count){
		this.term_count = term_count;
	}
 	public List<Terms> getTerms(){
		return this.terms;
	}
	public void setTerms(List terms){
		this.terms = terms;
	}
 	public String getTitle(){
		return this.title;
	}
	public void setTitle(String title){
		this.title = title;
	}
 	public String getUrl(){
		return this.url;
	}
	public void setUrl(String url){
		this.url = url;
	}
 	public String getVisibility(){
		return this.visibility;
	}
	public void setVisibility(String visibility){
		this.visibility = visibility;
	}
}
