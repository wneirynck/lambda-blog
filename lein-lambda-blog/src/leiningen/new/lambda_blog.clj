(ns leiningen.new.lambda-blog
  (:require [leiningen.new.templates :refer [renderer name-to-path ->files]]
            [leiningen.core.main :as main]))

(defn lambda-blog
  "Creates a λ-blog blog named `name`."
  [name]
  (let [data {:author "me"
              :name name
              :now (java.util.Date.)
              :sanitized (name-to-path name)}
        render (renderer "lambda-blog")]
    (main/info "Generating fresh 'lein new' lambda-blog project.")
    (->files data
             ["project.clj" (render "project.clj" data)]
             ["resources/entries/hello.md" (render "entry.md" data)]
             ["resources/css/{{sanitized}}.css" (render "style.css" data)]
             ["resources/js/{{sanitized}}.js" (render "scripts.js" data)]
             ["resources/static/static.md" (render "static.md" data)]
             ["src/{{sanitized}}/core.clj" (render "core.clj" data)])))
