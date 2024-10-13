// components/Footer.jsx
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { faTwitter, faGithub, faSlack, faYoutube } from '@fortawesome/free-brands-svg-icons'
import { Button } from '../ui/button';

export default function Footer() {
  return (
    <footer className="bg-[#000000] text-white py-8 md:py-16">
      <div className="container mx-auto px-4">
        <div className="border-t border-[#ffffff1a] pt-8 md:pt-16">
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-8 text-center">
            <div className="mb-8 sm:mb-0">
              <p className="font-semibold mb-4 font-mono">Quagmire</p>
              {/* Social Icons */}
              <div className="flex justify-center space-x-4 mt-4">
                <Button className="bg-[#ffffff1a] text-white hover:bg-[#ffffff2a]">
                  <FontAwesomeIcon icon={faTwitter} className="w-5 h-5" />
                </Button>
                <Button className="bg-[#ffffff1a] text-white hover:bg-[#ffffff2a]">
                  <FontAwesomeIcon icon={faGithub} className="w-5 h-5" />
                </Button>
                <Button className="bg-[#ffffff1a] text-white hover:bg-[#ffffff2a]">
                  <FontAwesomeIcon icon={faSlack} className="w-5 h-5" />
                </Button>
                <Button className="bg-[#ffffff1a] text-white hover:bg-[#ffffff2a]">
                  <FontAwesomeIcon icon={faYoutube} className="w-5 h-5" />
                </Button>
              </div>
            </div>
            <div className="mb-8 sm:mb-0">
              <h3 className="font-semibold mb-4">Product</h3>
              <ul className="space-y-2">
                <li><a href="/" className="text-[#ffffffb3] hover:text-white">Features</a></li>
                <li><a href="/" className="text-[#ffffffb3] hover:text-white">Integrations</a></li>
                <li><a href="/" className="text-[#ffffffb3] hover:text-white">Changelog</a></li>
                <li><a href="/" className="text-[#ffffffb3] hover:text-white">Docs</a></li>
                <li><a href="/" className="text-[#ffffffb3] hover:text-white">Downloads</a></li>
              </ul>
            </div>
          </div>
        </div>
      </div>
    </footer>
  );
}