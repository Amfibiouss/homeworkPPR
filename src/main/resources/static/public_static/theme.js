
function applyThemeHandler() {

    if (document.cookie.match("theme=white") &&  theme.getAttribute("href") != "/public_static/white_theme.css") {
        theme.setAttribute("href", "/public_static/white_theme.css");
    }
    if (document.cookie.match("theme=black") &&  theme.getAttribute("href") != "/public_static/black_theme.css") {
        theme.setAttribute("href", "/public_static/black_theme.css");
    }
}

document.addEventListener("DOMContentLoaded", function() {

    switch_theme.onclick = function() {

        if (document.cookie.match("theme=black")) {
            document.cookie = "theme=white; path=/;";
        }
        else {
            if (document.cookie.match("theme=white")){
                document.cookie = "theme=black; path=/;";
            }
            else {
                document.cookie = "theme=white; path=/;";
            }
        }
        applyThemeHandler();
    };

    applyThemeHandler();
});