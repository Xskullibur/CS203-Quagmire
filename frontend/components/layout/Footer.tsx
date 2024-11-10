// components/Footer.jsx
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { faTwitter, faGithub, faSlack, faYoutube } from '@fortawesome/free-brands-svg-icons'
import { Button } from '../ui/button';

export default function Footer() {
  return (
    <footer className="bg-[#000000] text-white">
      <div className="border-t border-[#ffffff1a] pt-8 md:pt-16 py-20 text-center">
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
    </footer>
  );
}