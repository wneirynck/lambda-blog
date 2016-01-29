(ns lambda-blog.templates.entries
  (:refer-clojure :exclude [time])
  (:require [lambda-blog.templates.bits :refer [info-label panel row text-centered]]
            [lambda-blog.templates.page :refer [page]]
            [lambda-blog.utils :refer [format-time pathcat]]
            [s-html.tags :refer [a article div footer h1 header hr i li nav p span time ul]]))

(defn- entry-tags [{:keys [path-to-root tags]}]
  (nav (map #(a {:class :tag
                 :href (pathcat path-to-root (:path %))}
                (info-label (:id %))
                " ")
            (sort-by :id tags))))

(defn- pager [class url & contents]
  (nav (ul {:class :pager}
           (li {:class class}
               (apply a {:href url} contents)))))

(defn entry
  "Creates an HTML `atricle` representing an `ent`ry."
  [{:keys [author contents next path-to-root previous timestamp title] :as ent}]
  (article
   (header
    (panel
     (row
      (div {:class [:col-xs-2 :col-sm-3]}
           (when previous
             (pager :previous
                    (pathcat path-to-root (:path previous))
                    (i {:class [:fa :fa-chevron-left]})
                    " "
                    (span {:class [:hidden-xs]}
                          (:title previous)))))
      (div {:class [:col-xs-8 :col-sm-6]}
           (text-centered (h1 title)
                          (p "Posted on " (time (format-time "YYYY-MM-dd HH:mm" timestamp))
                             " by " author)
                          (entry-tags ent)))
      (div {:class [:col-xs-2 :col-sm-3]}
           (when next
             (pager :next
                    (pathcat path-to-root (:path next))
                    (span {:class [:hidden-xs]}
                          (:title next))
                    " "
                    (i {:class [:fa :fa-chevron-right]})))))))
   contents))

(defn entry-page
  "Creates an HTML page containing an `ent`ry formatted by [[entry]]."
  [ent]
  (page entry ent))

(defn embedded-entry
  "Creates an HTML article representing a summarized `entry` that will be embedded within another `ent`ity."
  [{:keys [path-to-root] :as ent} {:keys [author contents path tags timestamp title] :as entry}]
  (article
   (header
    (panel
     (text-centered
      (row
       (h1 (a {:href (pathcat path-to-root path)}
              title))
       (p "Posted on " (time (format-time "YYYY-MM-dd HH:mm" timestamp))
          " by " (or author (:author ent))) ;; KLUDGE :(
       (entry-tags ent)))))
   contents)) ;; FIXME This should be shortened somehow.

(defn recent-entries
  "Creates an HTML page containing a list of [[entry-summary]]'ies of `n` most recent `entries` and a link to the `archives`."
  [n {:keys [archives entries path-to-root] :as ent}]
  (page (fn [_]
          [(map (juxt (partial embedded-entry ent)
                      (constantly (hr)))
                (->> entries
                     (sort-by :timestamp)
                     reverse
                     (take n)))
           (-> (a {:href (pathcat path-to-root (:path archives))}
                  "Further reading...")
               h1
               text-centered
               panel)])
        ent))

(defn entries-by-tag
  "Creates an HTML page containing a list of [[entry-summary]]'ies of `entries` tagged with a tag identified by `id` and a link to the `archives`."
  [{:keys [archives entries id path-to-root] :as ent}]
  (page (fn [_]
          [(panel (text-centered (h1 (format "Tagged %s" id))))
           (map (juxt (partial embedded-entry ent)
                      (constantly (hr)))
                (->> entries
                     (filter (fn [{:keys [tags]}]
                               (contains? (into #{}
                                                (map :id tags))
                                          id)))
                     (sort-by :timestamp)
                     reverse))
           (-> (a {:href (pathcat path-to-root (:path archives))}
                  "Archives")
               h1
               text-centered
               panel)])
        ent))
