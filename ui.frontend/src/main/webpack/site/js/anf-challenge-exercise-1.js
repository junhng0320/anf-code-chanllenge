var ANF = {};

ANF.exercise1 = {
	init: () => {
		$.getJSON('/bin/getUserAgeRange',{},function(data){
		        ANF.exercise1.allowedAge = {};
				ANF.exercise1.allowedAge.min = Number(data.minAge);
				ANF.exercise1.allowedAge.max = Number(data.maxAge);
        });
        
        
        $('#exercise_1 form').submit(function(event){
        	event.preventDefault();
            if(!$('[name="first-name"]').val() || !$('[name="last-name"]').val() || !$('[name="age"]').val()) {
                alert('please fill data'); //could use jquery validation
            } else {
	            const fName = $('[name="first-name"]').val();
	        	const lName = $('[name="last-name"]').val();
	        	const age = Number($('[name="age"]').val());
	        	const countryDivTxt = $(".country__cmp").text();
	        	
	        	
	        	const countryArray = countryDivTxt.split(":");
				const country = countryArray[1].trim();
	        	
                if(ANF.exercise1.allowedAge.min <= age && ANF.exercise1.allowedAge.max >= age) {
                        $.post('/bin/saveUserDetails',{'fname':fName, 'lname':lName, 'age':age, 'country':country}).done(function(data){
                            console.log(data);
                            
                            alert('Data saved successfully!');
                            
                        });
                    } else {
                        alert("Age entered is not allow! Please enter age between " + ANF.exercise1.allowedAge.min + " and " +ANF.exercise1.allowedAge.max);
                    }
                
            }
            return false;
        });
          
	},
	
	 
	
};


$(document).ready(function() {
	
	if($('#exercise_1 form')){
				//should create a generic initPage function in real app for example initPage(selector, pageInitFunction)  
		        ANF.exercise1.init();
	}
});