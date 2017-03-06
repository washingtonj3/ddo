/*
 *  Copyright (c) 2016, University of Dayton
 *
 *  Licensed under the Educational Community License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *              http://opensource.org/licenses/ecl2
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

$(document).ready(function() {
    $("#notifyHelp").click(function(e){
        $("div.notifyTip").fadeIn(200).css("display","inline-block");
        e.stopPropagation();
    });
    $(document).click(function() {
        $("div.notifyTip").fadeOut(200);
    });
    $("div.notifyTip").click(function(e) {
        e.stopPropagation();
    });
    $("#instructorRequirements").focus(function(){
        $("div.reqTip").fadeIn(200).css("display","inline-block");
    });
    $("#instructorRequirements").blur(function(){
        $("div.reqTip").fadeOut(200);
    });
    $("#feedbackFocus").focus(function(){
        $("div.focusTip").fadeIn(200).css("display","inline-block");
    });
    $("#feedbackFocus").blur(function(){
        $("div.focusTip").fadeOut(200);
    });
    $("#closeLink").click(function(){
        $("#agree").fadeOut(200);
        $("#agreeMask").fadeOut(200);
    });
    $("#feedbackAssign").css("min-height",$("#feedbackSubmission").height());

    $(function() {
        if(localStorage) {
            if (localStorage.getItem('agreed')) {
                $("#agreeMask").hide();
                $("#agree").hide();
            }
        }
    });

    $("#closeLink").click(function() {
        localStorage.setItem("agreed", true);
        return true;
    });

    $("#courseTitle").change(function() {
        var selected = this.value;
        if(selected == "Other") {
            $("#courseOther").show();
            $("#courseOther").focus();
            if (window.name != "") {
                var frame = parent.document.getElementById(window.name);
                if (frame) {
                    var newH = frame.height + 50;
                    $(frame).height(newH);
                }
            }
        } else {
            $("#courseOther").hide();
        }
    });

    $("#instructors").change(function() {
        var selected = this.value;
        if(selected == "Other") {
            $("#instructorNameOther").show();
            $("#instructorEmailOther").show();
            $("#instructorNameOther").focus();
            //resize iframe
            if (window.name != "") {
                var frame = parent.document.getElementById(window.name);
                if (frame) {
                    var newH = frame.height + 50;
                    $(frame).height(newH);
                }
            }
        } else {
            $("#instructorNameOther").hide();
            $("#instructorEmailOther").hide();
        }

        if(selected != "" && selected != "Other") {
            $("#notifyInstructor").removeAttr("disabled");
        } else {
            var re = /^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/;
            if(re.test($("#instructorEmailOther").val())) {
                $("#notifyInstructor").removeAttr("disabled");
            } else {
                $("#notifyInstructor").attr("disabled", true);
            }
        }
    });

    $("#instructorEmailOther").keyup(function() {
        var re = /^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/;
        var email = this.value;
        if(re.test(email)) {
            $("#notifyInstructor").removeAttr("disabled");
        } else {
            $("#notifyInstructor").attr("disabled", true);
        }
    });
    $("#instructorEmailOther").change(function() {
        var re = /^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/;
        var email = this.value;
        if(re.test(email)) {
            $("#notifyInstructor").removeAttr("disabled");
        } else {
            $("#notifyInstructor").attr("disabled", true);
        }
    });
    $("#instructorEmailOther").blur(function() {
        var re = /^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/;
        var email = this.value;
        if(re.test(email)) {
            $("#notifyInstructor").removeAttr("disabled");
        } else {
            $("#notifyInstructor").attr("disabled", true);
        }
    });
    $("#replaceFile").click(function() {
        $("#newAttachment").toggle();
        $("#replaceReview").prop("checked", !$("#replaceReview").prop("checked"));
    });
    $("#replaceReview").click(function() {
        $("#newAttachment").toggle();
    });
    $('.confirmation').on('click', function () {
        return confirm('Are you sure you want to archive all reviewed submissions?');
    });
});