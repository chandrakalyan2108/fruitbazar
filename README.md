# FruitBazar — Fruit Delivery Website

A Maven-based Java web application (WAR), deployed to Tomcat 9 and automated with Jenkins.

## Stack
- Java 21, Servlet API 4.0 (annotations, no servlet XML mappings needed)
- JSP + JSTL for views
- Maven (`maven-war-plugin`) for packaging
- Session-based shopping cart (no database required)
- Jenkins pipeline for CI/CD to a remote Tomcat server over SSH

## Pages
| Page | File |
|---|---|
| Home | `index.jsp` |
| About Us | `about.jsp` |
| Fruits / Gallery | `fruits.jsp` |
| Cart | `cart.jsp` |
| Checkout | `checkout.jsp` + `orderconfirmation.jsp` |
| Login | `login.jsp` |
| FAQ | `faq.jsp` |
| Error | `error.jsp` |

## Demo login
```
username: demo
password: demo123
```
(Stored in-memory in `UserStore.java` — swap for a real database + hashed passwords before production use.)

## Local build
```bash
mvn clean package
```
This produces `target/FruitBazar.war`.

## Deploying manually
```bash
cp target/FruitBazar.war /opt/tomcat/webapps/fruitbazar.war
sudo /opt/tomcat/bin/shutdown.sh || true
sleep 5
sudo /opt/tomcat/bin/startup.sh
```
Then visit: `http://<server-ip>:8080/fruitbazar`

## Jenkins setup
1. Push this project to a GitHub repo.
2. In the `Jenkinsfile`, replace `YOUR_GITHUB_USERNAME/fruitbazar.git` with your actual repo URL.
3. In Jenkins, add a "Secret text" credential with ID `fruitbazar-server-ip` containing your
   Tomcat server's IP address.
4. Create a Pipeline job pointing at this repo — the included `Jenkinsfile` will:
   - Check out the code
   - SSH into your server
   - `git clone` + `mvn clean package`
   - Replace the deployed WAR
   - Restart Tomcat

## Images
Fruit photos load via `picsum.photos` with fixed per-fruit seeds (deterministic, so the
same fruit always shows the same photo). This is the same image service already proven
reliable on your event management site. It is not Flickr-backed, so it avoids the slow/
blocked-image issue that `loremflickr.com` can have on some networks.

If you want literal photos of each specific fruit (not just a styled placeholder), replace
the `imageUrl` values in `FruitCatalog.java` with your own hosted image URLs, or drop image
files into `src/main/webapp/images/` and reference them as
`${pageContext.request.contextPath}/images/mango.jpg`.

## Notes / next steps
- Cart and login are in-memory (per session / per app restart) — add a real database
  (e.g. MySQL) for persistent orders and user accounts.
- No payment gateway is wired up; checkout currently just records the order and clears the cart.
