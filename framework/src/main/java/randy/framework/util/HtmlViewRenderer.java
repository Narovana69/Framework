package randy.framework.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import randy.framework.helper.ViewRenderer;

public class HtmlViewRenderer implements ViewRenderer {
    @Override
    public void render(HttpServletRequest request, HttpServletResponse response, String viewPath)
            throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (InputStream in = request.getServletContext().getResourceAsStream(viewPath)) {
            if (in == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Vue introuvable : " + viewPath);
                return;
            }
            // in.transferTo(response.getOutputStream());
            // 1- Lecture du fichier
            String htmlContent = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            // 2- Dynamisation: parcours les attributs renvoyes du controlleurr
            Enumeration<String> attributeNames = request.getAttributeNames();
            while (attributeNames.hasMoreElements()) {
                String key = attributeNames.nextElement();
                Object value = request.getAttribute(key);
                if (value != null) {
                    String replacement;
                    // Si l'attribut est un tableau (comme ton String[] de messages)
                    if (value instanceof String[] array) {
                        StringBuilder sb = new StringBuilder("<ul>");
                        for (String item : array) {
                            sb.append("<li>").append(item).append("</li>");
                        }
                        sb.append("</ul>");
                        replacement = sb.toString();
                    }
                    // Si c'est un objet classique (String, Integer, etc.), on prend son texte
                    else {
                        replacement = value.toString();
                    }
                    // On remplace la balise {clé} par sa valeur dans tout le fichier HTML
                    htmlContent = htmlContent.replace("{" + key + "}", replacement);
                }
            }
            response.getWriter().write(htmlContent);
        }
    }
}